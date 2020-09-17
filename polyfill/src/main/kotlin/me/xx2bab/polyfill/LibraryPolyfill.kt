package me.xx2bab.polyfill

import com.android.build.api.variant.VariantProperties
import me.xx2bab.polyfill.matrix.base.LibraryAGPTaskListener
import org.gradle.api.Project

class LibraryPolyfill(private val project: Project) : Polyfill(project) {

    fun addAGPTaskListener(variant: VariantProperties, listener: LibraryAGPTaskListener) {
        listener.onVariantProperties(project,
                androidExtension,
                variant,
                variant.name.capitalize())
    }

}