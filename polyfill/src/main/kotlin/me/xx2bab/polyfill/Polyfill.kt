package me.xx2bab.polyfill

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.VariantProperties
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import me.xx2bab.polyfill.Polyfill.Companion.createApplicationPolyfill
import me.xx2bab.polyfill.Polyfill.Companion.createLibraryPolyfill
import me.xx2bab.polyfill.matrix.base.AGPTaskListener
import me.xx2bab.polyfill.matrix.base.SelfManageableProvider
import org.gradle.api.Action
import org.gradle.api.Project

typealias androidExt = CommonExtension<*, *, *, *, *, *, *, *>

/**
 * The entry for overall API calls. Polyfill itself doesn't allow instance constructing,
 * should always create its subclasses via [createApplicationPolyfill] or [createLibraryPolyfill],
 * to support their own [AGPTaskListener] respectively.
 */
open class Polyfill internal constructor(private val project: Project) {

    companion object {
        fun createApplicationPolyfill(project: Project): ApplicationPolyfill {
            if (!checkSupportedGradleVersion()) {
                throw UnsupportedAGPVersionException("Required minimum Android Gradle Plugin version is: ")
            }
            if (!project.plugins.hasPlugin(AppPlugin::class.java)) {
                throw UnsupportedAGPVersionException("Required Android AppPlugin.")
            }
            return ApplicationPolyfill(project)
        }

        fun createLibraryPolyfill(project: Project): LibraryPolyfill {
            if (!checkSupportedGradleVersion()) {
                throw UnsupportedAGPVersionException("Required minimum Android Gradle Plugin version is: ")
            }
            if (!project.plugins.hasPlugin(LibraryPlugin::class.java)) {
                throw UnsupportedAGPVersionException("Required Android LibraryPlugin.")
            }
            return LibraryPolyfill(project)
        }

        private fun checkSupportedGradleVersion(): Boolean {
            return true
        }
    }

    protected val providers = ProviderCache()
    protected val androidExtension: androidExt

    init {
        if (project.extensions.findByType(CommonExtension::class.java) == null) {
            throw IllegalArgumentException("Required Application or Library Extensions.")
        } else {
            androidExtension = project.extensions.findByType(CommonExtension::class.java)!!
        }
    }


    fun <T : SelfManageableProvider<*>> getProvider(variant: VariantProperties, clazz: Class<T>): T {
        return providers.getProvider(clazz, project, androidExtension, variant)
    }

    fun onVariantProperties(action: Action<VariantProperties>) {
        androidExtension.onVariantProperties { action.execute(this) }
    }

    fun addAGPTaskListener(variant: VariantProperties, listener: AGPTaskListener) {
        listener.onVariantProperties(project,
                androidExtension,
                variant,
                variant.name.capitalize())
    }

    class UnsupportedAGPVersionException(msg: String) : Exception(msg)

}
