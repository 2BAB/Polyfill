package me.xx2bab.polyfill

import com.android.Version
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import me.xx2bab.polyfill.gradle.tool.SemanticVersionLite
import org.gradle.api.Project

/**
 * A simple factory to create Polyfill instances.
 */
object PolyfillFactory {

    fun createApplicationPolyfill(project: Project): ApplicationPolyfill {
        if (!checkSupportedGradleVersion()) {
            throw Polyfill.UnsupportedAGPVersionException("Required minimum Android Gradle Plugin version is: ")
        }
        if (!project.plugins.hasPlugin(AppPlugin::class.java)) {
            throw Polyfill.UnsupportedAGPVersionException("'com.android.application' plugin required.")
        }
        return ApplicationPolyfill(project)
    }

    fun createLibraryPolyfill(project: Project): LibraryPolyfill {
        if (!checkSupportedGradleVersion()) {
            throw Polyfill.UnsupportedAGPVersionException("Required minimum Android Gradle Plugin version is: ")
        }
        if (!project.plugins.hasPlugin(LibraryPlugin::class.java)) {
            throw Polyfill.UnsupportedAGPVersionException("'com.android.library' plugin required.")
        }
        return LibraryPolyfill(project)
    }

    fun checkSupportedGradleVersion(): Boolean {
        val curr = SemanticVersionLite(Version.ANDROID_GRADLE_PLUGIN_VERSION)
        val min = SemanticVersionLite("4.2")
        val max = SemanticVersionLite("7.0")
        return curr >= min && curr < max
    }


}