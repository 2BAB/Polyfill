package me.xx2bab.polyfill

import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.matrix.base.ApplicationAGPTaskListener
import me.xx2bab.polyfill.matrix.base.ApplicationSelfManageableProvider
import org.gradle.api.Project

class ApplicationPolyfill(private val project: Project) : Polyfill(project) {

    fun addAGPTaskListener(variant: Variant, listener: ApplicationAGPTaskListener) {
        listener.onVariantProperties(project,
                androidExtension,
                variant,
                variant.name.capitalize())
    }

    fun <T : ApplicationSelfManageableProvider<*>> getProvider(variant: Variant, clazz: Class<T>): T {
        return providers.getProvider(clazz, project, androidExtension, variant)
    }

}