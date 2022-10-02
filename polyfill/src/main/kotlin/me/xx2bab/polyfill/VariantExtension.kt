package me.xx2bab.polyfill

import com.android.Version
import com.android.build.api.component.analytics.AnalyticsEnabledApplicationVariant
import com.android.build.api.component.analytics.AnalyticsEnabledLibraryVariant
import com.android.build.api.component.impl.ApkCreationConfigImpl
import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.LibraryVariant
import com.android.build.api.variant.Variant
import com.android.build.api.variant.impl.ApplicationVariantImpl
import com.android.build.api.variant.impl.LibraryVariantImpl
import com.android.build.gradle.internal.plugins.BasePlugin
import com.android.build.gradle.internal.scope.MutableTaskContainer
import com.android.build.gradle.internal.services.VersionedSdkLoaderService
import com.android.sdklib.BuildToolInfo
import me.xx2bab.polyfill.tools.ReflectionKit
import me.xx2bab.polyfill.tools.SemanticVersionLite
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.configurationcache.extensions.capitalized


////////// Common Variant //////////

/**
 * `kotlin-dsl` has compatible issues with replaceFirstChar(),
 * so we use this deprecated method instead as a workaround.
 * To capitalized first letter for task name usage.
 */
fun Variant.getCapitalizedName() = name.capitalized()

/**
 * To get current Android Gradle Plugin version.
 */
fun Variant.getAgpVersion() = SemanticVersionLite(Version.ANDROID_GRADLE_PLUGIN_VERSION)

/**
 * To get BuildToolInfo instance provider, later you can use like below to retrieve some tools' information.
 * e.g. `buildToolInfoProvider.get().getPath(BuildToolInfo.PathId.AAPT2)`
 *
 * @return [BuildToolInfo] wrapped by [Provider].
 */
fun Variant.getBuildToolInfo(project: Project): Provider<BuildToolInfo> {
    return BuildToolInfoPatch(this).applyOrDefault {
        val plugin = when (this) {
            is ApplicationVariant -> {
                project.plugins.getPlugin(com.android.build.gradle.internal.plugins.AppPlugin::class.java)
            }
            is LibraryVariant -> {
                project.plugins.getPlugin(com.android.build.gradle.internal.plugins.LibraryPlugin::class.java)
            }
            else -> {
                throw UnsupportedOperationException("Can not find corresponding plugin associated to $this.")
            }
        }
        val sdkLoaderService = ReflectionKit.getField(
            BasePlugin::class.java,
            plugin, "versionedSdkLoaderService"
        ) as VersionedSdkLoaderService
        sdkLoaderService.versionedSdkLoader.get().buildToolInfoProvider
    }
}


////////// ApplicationVariant //////////

/**
 * Casting ApplicationVariant to its actual implementation.
 * This is helpful as Variant instance is one of the most important public API
 * for us to interact with AGP. It contains a bunch of tools / data providers
 * to access more intermediates of the Android build.
 *
 * @return [ApplicationVariantImpl]
 */
fun ApplicationVariant.getApplicationVariantImpl(): ApplicationVariantImpl {
    return when (this) {
        is ApplicationVariantImpl -> {
            this
        }
        is AnalyticsEnabledApplicationVariant -> {
            this.delegate as ApplicationVariantImpl
        }
        else -> {
            throw UnsupportedOperationException("Can not convert $this to ApplicationVariantImpl.")
        }
    }
}

/**
 * Indirect implementation of [com.android.build.gradle.internal.component.ApkCreationConfig].
 * The [ApkCreationConfigImpl.config] provides internal Artifacts APIs that can be consumed
 * for more intermediate files.
 *
 * @return [ApkCreationConfigImpl]
 */
fun ApplicationVariant.getApkCreationConfigImpl() = getApplicationVariantImpl().delegate


/**
 * To access partial common used AGP [TaskProvider]s.
 * For example the [MutableTaskContainer.assembAleTask].
 *
 * @return [MutableTaskContainer]
 */
fun ApplicationVariant.getTaskContainer() = getApkCreationConfigImpl().config.taskContainer


////////// LibraryVariant //////////

/**
 * Same as [getApplicationVariantImpl], but is used for LibraryVariant.
 *
 * @return [LibraryVariantImpl]
 */
fun LibraryVariant.getLibraryVariantImpl(): LibraryVariantImpl {
    return when (this) {
        is LibraryVariantImpl -> {
            this
        }
        is AnalyticsEnabledLibraryVariant -> {
            this.delegate as LibraryVariantImpl
        }
        else -> {
            throw UnsupportedOperationException("Can not convert $this to LibraryVariantImpl.")
        }
    }
}
