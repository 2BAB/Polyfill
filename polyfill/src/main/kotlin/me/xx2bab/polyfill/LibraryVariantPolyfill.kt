package me.xx2bab.polyfill

import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.matrix.base.LibraryAGPTaskAction
import me.xx2bab.polyfill.matrix.base.LibrarySelfManageableProvider
import org.gradle.api.Project

class LibraryVariantPolyfill(project: Project, variant: Variant) : Polyfill<
        LibraryAGPTaskAction,
        LibrarySelfManageableProvider<*>>(project, variant) {

    override fun addAGPTaskAction(action: LibraryAGPTaskAction) {
        action.onVariants(
            project,
            androidExtension,
            variant,
            variant.name.capitalize()
        )
    }

    override fun <T : LibrarySelfManageableProvider<*>> newProvider(clazz: Class<T>): T {
        val instance = providers.newProviderInstance(clazz)
        instance.initialize(project, androidExtension, variant)
        return instance
    }

}