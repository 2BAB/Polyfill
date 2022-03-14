package me.xx2bab.polyfill

import com.android.Version
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.DslExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import me.xx2bab.polyfill.artifact.ApplicationArtifactsStorage
import me.xx2bab.polyfill.artifact.DefaultArtifactsStorage
import me.xx2bab.polyfill.artifact.LibraryArtifactsStorage
import me.xx2bab.polyfill.tools.SemanticVersionLite
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType

class PolyfillPlugin : Plugin<Project> {

    private val artifactsPolyfills = mutableListOf<DefaultArtifactsStorage<*>>()

    override fun apply(project: Project) {
        checkSupportedGradleVersion()
        val ext = project.extensions.create<PolyfillExtension>("artifactsPolyfill")

        project.plugins.withType<AppPlugin> {
            val androidExt = project.extensions.getByType(
                ApplicationAndroidComponentsExtension::class.java
            )

            val hackyDslExt = DslExtension.Builder(ApplicationArtifactsStorage::class.simpleName!!).build()
            androidExt.registerExtension(hackyDslExt) { variantExtConfig ->
                val artifactsPolyfill = ApplicationArtifactsStorage(project, variantExtConfig.variant)
                artifactsPolyfills.add(artifactsPolyfill)
                artifactsPolyfill
            }
            androidExt.finalizeDsl {
                ext.locked.set(true)
            }
        }

        project.plugins.withType<LibraryPlugin> {
            val androidExt = project.extensions.getByType(
                LibraryAndroidComponentsExtension::class.java
            )
            val hackyDslExt = DslExtension.Builder(LibraryArtifactsStorage::class.simpleName!!).build()
            androidExt.registerExtension(hackyDslExt) { variantExtConfig ->
                val artifactsPolyfill = LibraryArtifactsStorage(project, variantExtConfig.variant)
                artifactsPolyfills.add(artifactsPolyfill)
                artifactsPolyfill
            }
            androidExt.finalizeDsl {
                ext.locked.set(true)
            }
        }

    }

    private fun checkSupportedGradleVersion() {
        val curr = SemanticVersionLite(Version.ANDROID_GRADLE_PLUGIN_VERSION)
        val min = SemanticVersionLite("7.1")
        val max = SemanticVersionLite("7.2")
        if (!(curr in min..max)) {
            throw throw UnsupportedAGPVersionException("Required minimum Android Gradle Plugin version 7.1")
        }
    }

    class UnsupportedAGPVersionException(msg: String) : Exception(msg)

}