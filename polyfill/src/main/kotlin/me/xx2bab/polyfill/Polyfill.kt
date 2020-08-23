package me.xx2bab.polyfill

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.*
import com.android.build.gradle.api.BaseVariant
import me.xx2bab.polyfill.matrix.base.AGPTaskListener
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project


class Polyfill(val project: Project) {

    private val listeners = mutableListOf<AGPTaskListener>()
    private val androidExtension: CommonExtension<*, *, *, *, *, *, *, *>
    private val androidExtensionClassic: BaseExtension
    private val variantsClassic: DomainObjectSet<out BaseVariant>

    init {
        if (!checkSupportedGradleVersion()) {
            throw UnsupportedAGPVersionException("Required minimum Android Gradle Plugin version is: ")
        }

        if (project.extensions.findByType(CommonExtension::class.java) == null) {
            throw IllegalArgumentException("Required Application or Library Extensions.")
        } else {
            androidExtension = project.extensions.findByType(CommonExtension::class.java)!!
        }

        when {
            project.plugins.hasPlugin(AppPlugin::class.java) -> {
                androidExtensionClassic = project.extensions.findByType(AppExtension::class.java)!!
                val app = androidExtensionClassic as AppExtension
                variantsClassic = app.applicationVariants

            }
            project.plugins.hasPlugin(LibraryPlugin::class.java) -> {
                androidExtensionClassic = project.extensions.findByType(LibraryExtension::class.java)!!
                val lib = androidExtensionClassic as LibraryExtension
                variantsClassic = lib.libraryVariants
            }
            else -> {
                throw IllegalArgumentException("Required Application or Library Extensions.")
            }
        }
    }

    private fun checkSupportedGradleVersion(): Boolean {
        return true
    }

    fun addOnAGPTaskListener(listener: AGPTaskListener) {
        listeners.add(listener)
        project.afterEvaluate {
            listener.onProjectEvaluated(project, variantsClassic)
        }
        variantsClassic.forEach { variant ->
            listener.onVariantClassicProperties(project,
                    androidExtensionClassic,
                    variant,
                    variant.name.capitalize())
        }
        androidExtension.onVariantProperties {
            listener.onVariantProperties(project,
                    androidExtension,
                    this,
                    this.name.capitalize())
        }
    }

    class UnsupportedAGPVersionException(msg: String) : Exception(msg)

}
