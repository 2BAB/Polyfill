package me.xx2bab.polyfill

import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.matrix.base.LibraryAGPTaskListener
import me.xx2bab.polyfill.matrix.base.LibrarySelfManageableProvider
import org.gradle.api.Project

class LibraryPolyfill(private val project: Project) : Polyfill(project) {

    fun addAGPTaskListener(variant: Variant, listener: LibraryAGPTaskListener) {
        listener.onVariantProperties(project,
                androidExtension,
                variant,
                variant.name.capitalize())
    }

    fun <T : LibrarySelfManageableProvider<*>> getProvider(variant: Variant, clazz: Class<T>): T {
        return providers.getProvider(clazz, project, androidExtension, variant)
    }

}