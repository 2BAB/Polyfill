package me.xx2bab.polyfill.agp

import com.android.Version
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.base.ApplicationSelfManageableProvider
import me.xx2bab.polyfill.base.LibrarySelfManageableProvider
import me.xx2bab.polyfill.gradle.SemanticVersionLite
import org.gradle.api.Project

/**
 * To get the Android Gradle Plugin version that the user is currently using.
 * e.g. 3.6.3, 4.0.0
 *
 * The result will be formatted by [SemanticVersionLite].
 */
class AGPVersionProvider: ApplicationSelfManageableProvider<SemanticVersionLite>,
    LibrarySelfManageableProvider<SemanticVersionLite> {

    private var agpVersion: SemanticVersionLite = SemanticVersionLite(Version.ANDROID_GRADLE_PLUGIN_VERSION)

    override fun initialize(project: Project,
                            androidExtension: AndroidComponentsExtension<*, *, *>,
                            variant: Variant) {
        // Could be ignored
    }

    override fun obtain(defaultValue: SemanticVersionLite?): SemanticVersionLite {
        return agpVersion
    }

}