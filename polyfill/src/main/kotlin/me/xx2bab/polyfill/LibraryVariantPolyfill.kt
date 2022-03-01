package me.xx2bab.polyfill

import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.base.LibraryAGPTaskAction
import me.xx2bab.polyfill.base.LibrarySelfManageableProvider
import org.gradle.api.Project

class LibraryVariantPolyfill(project: Project, variant: Variant) : Polyfill<
        LibraryAGPTaskAction,
        LibrarySelfManageableProvider<*>>(project, variant) {

    override fun addAGPTaskAction(action: LibraryAGPTaskAction) {
        action.orchestrate(
            project,
            androidExtension,
            variant,
            variant.name.capitalize()
        )
    }

    override fun <T : LibrarySelfManageableProvider<*>> newProvider(clazz: Class<T>): T {
        val instance = clazz.getDeclaredConstructor().newInstance()
        instance.initialize(project, androidExtension, variant)
        return instance
    }

}