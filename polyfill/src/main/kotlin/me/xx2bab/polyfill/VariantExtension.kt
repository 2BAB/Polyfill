package me.xx2bab.polyfill

import com.android.Version
import com.android.build.api.component.analytics.AnalyticsEnabledApplicationVariant
import com.android.build.api.component.analytics.AnalyticsEnabledLibraryVariant
import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.LibraryVariant
import com.android.build.api.variant.Variant
import com.android.build.api.variant.impl.ApplicationVariantImpl
import com.android.build.api.variant.impl.LibraryVariantImpl
import com.android.sdklib.BuildToolInfo
import me.xx2bab.polyfill.tools.SemanticVersionLite
import org.gradle.api.provider.Provider
import org.gradle.configurationcache.extensions.capitalized

////////// Common Variant //////////

fun Variant.getCapitalizedName() = name.capitalized()

fun Variant.getAgpVersion() = SemanticVersionLite(Version.ANDROID_GRADLE_PLUGIN_VERSION)

fun Variant.getBuildToolInfo(): Provider<BuildToolInfo> {
    val globalScope = when (this) {
        is ApplicationVariant -> {
            this.getGlobalScope()
        }
        is LibraryVariant -> {
            this.getGlobalScope()
        }
        else -> {
            throw UnsupportedOperationException("Can not convert $this to either ApplicationVariantImpl or LibraryVariantImpl.")
        }
    }
    return globalScope.versionedSdkLoader.flatMap { it.buildToolInfoProvider }
}

////////// ApplicationVariant //////////

fun ApplicationVariant.getApplicationVariantImpl() : ApplicationVariantImpl {
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

fun ApplicationVariant.getApkCreationConfigImpl() = getApplicationVariantImpl().delegate

fun ApplicationVariant.getGlobalScope() = getApkCreationConfigImpl().globalScope

fun ApplicationVariant.getTaskContainer() = getApkCreationConfigImpl().config.taskContainer



////////// LibraryVariant //////////

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

//fun LibraryVariant.getLibCreationConfigImpl() = getLibraryVariantImpl()

fun LibraryVariant.getGlobalScope() = getLibraryVariantImpl().globalScope
