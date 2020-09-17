package me.xx2bab.polyfill

import com.android.build.api.variant.VariantProperties
import me.xx2bab.polyfill.matrix.base.ApplicationAGPTaskListener
import org.gradle.api.Project

class ApplicationPolyfill(private val project: Project) : Polyfill(project) {

    fun addAGPTaskListener(variant: VariantProperties, listener: ApplicationAGPTaskListener) {
        listener.onVariantProperties(project,
                androidExtension,
                variant,
                variant.name.capitalize())
    }


}