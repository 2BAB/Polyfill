package me.xx2bab.polyfill

import com.android.Version
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import me.xx2bab.polyfill.artifact.ApplicationArtifactsStorage
import me.xx2bab.polyfill.artifact.LibraryArtifactsStorage
import me.xx2bab.polyfill.tools.SemanticVersionLite
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType

class PolyfillPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        checkSupportedGradleVersion()
        val ext = project.extensions.create<PolyfillExtension>("artifactsPolyfill")

        project.plugins.withType<AppPlugin> {
            val androidExt = project.extensions.getByType(
                ApplicationAndroidComponentsExtension::class.java
            )
            androidExt.finalizeDsl {
                ext.locked.set(true)
            }
            androidExt.beforeVariants { variant ->
                variant.registerExtension(
                    ApplicationArtifactsStorage::class.java,
                    ApplicationArtifactsStorage(project)
                )
            }
        }

        project.plugins.withType<LibraryPlugin> {
            val androidExt = project.extensions.getByType(
                LibraryAndroidComponentsExtension::class.java
            )
            androidExt.finalizeDsl {
                ext.locked.set(true)
            }
            androidExt.beforeVariants { variant ->
                variant.registerExtension(
                    LibraryArtifactsStorage::class.java,
                    LibraryArtifactsStorage(project)
                )
            }
        }
    }

    private fun checkSupportedGradleVersion() {
        val curr = SemanticVersionLite(Version.ANDROID_GRADLE_PLUGIN_VERSION)
        val min = SemanticVersionLite("7.1")
        val max = SemanticVersionLite("7.2")
        if (!(curr >= min && curr < max)) {
            throw throw UnsupportedAGPVersionException("Required minimum Android Gradle Plugin version 7.1")
        }
    }

    class UnsupportedAGPVersionException(msg: String) : Exception(msg)
}