package me.xx2bab.polyfill

import com.android.Version
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.DslExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import me.xx2bab.polyfill.artifact.ApplicationArtifactsRepository
import me.xx2bab.polyfill.artifact.DefaultArtifactsRepository
import me.xx2bab.polyfill.artifact.LibraryArtifactsRepository
import me.xx2bab.polyfill.tools.SemanticVersionLite
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType

class PolyfillPlugin : Plugin<Project> {

    private val artifactsPolyfills = mutableListOf<DefaultArtifactsRepository<*>>()

    override fun apply(project: Project) {
        checkSupportedGradleVersion()
        val ext = project.extensions.create<PolyfillExtension>("artifactsPolyfill")

        project.plugins.withType<AppPlugin> {
            val androidExt = project.extensions.getByType(
                ApplicationAndroidComponentsExtension::class.java
            )

            val hackyDslExt = DslExtension.Builder(ApplicationArtifactsRepository::class.simpleName!!).build()
            androidExt.registerExtension(hackyDslExt) { variantExtConfig ->
                val artifactsPolyfill = ApplicationArtifactsRepository(project, variantExtConfig.variant)
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
            val hackyDslExt = DslExtension.Builder(LibraryArtifactsRepository::class.simpleName!!).build()
            androidExt.registerExtension(hackyDslExt) { variantExtConfig ->
                val artifactsPolyfill = LibraryArtifactsRepository(project, variantExtConfig.variant)
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
        val max = SemanticVersionLite("7.3")
        if (curr !in min..max) {
            throw throw UnsupportedAGPVersionException("Required minimum Android Gradle Plugin version 7.1")
        }
    }

    class UnsupportedAGPVersionException(msg: String) : Exception(msg)

}