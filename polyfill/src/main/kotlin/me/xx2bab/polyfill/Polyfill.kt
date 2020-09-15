package me.xx2bab.polyfill

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.VariantProperties
import me.xx2bab.polyfill.matrix.base.AGPTaskListener
import me.xx2bab.polyfill.matrix.base.SelfManageableProvider
import org.gradle.api.Action
import org.gradle.api.Project

typealias androidExt = CommonExtension<*, *, *, *, *, *, *, *>

class Polyfill(private val project: Project) {

    private val providers = ProviderCache()

    private val androidExtension: androidExt

    init {
        if (!checkSupportedGradleVersion()) {
            throw UnsupportedAGPVersionException("Required minimum Android Gradle Plugin version is: ")
        }

        if (project.extensions.findByType(CommonExtension::class.java) == null) {
            throw IllegalArgumentException("Required Application or Library Extensions.")
        } else {
            androidExtension = project.extensions.findByType(CommonExtension::class.java)!!
        }
    }

    private fun checkSupportedGradleVersion(): Boolean {
        return true
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
