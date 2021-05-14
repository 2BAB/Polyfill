package me.xx2bab.polyfill

import com.android.build.api.variant.Variant
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.LibraryVariant
import me.xx2bab.polyfill.matrix.base.LibraryAGPTaskAction
import me.xx2bab.polyfill.matrix.base.LibrarySelfManageableProvider
import org.gradle.api.Action
import org.gradle.api.Project

class LibraryPolyfill(project: Project) : Polyfill<LibraryVariant,
        LibraryAGPTaskAction,
        LibrarySelfManageableProvider<*>>(project) {

    private val libClassicExt = project.extensions.findByType(LibraryExtension::class.java)!!

    override fun onClassicVariants(action: Action<LibraryVariant>) {
        libClassicExt.libraryVariants.all { v ->
            action.execute(v)
            true
        }
    }

    override fun addAGPTaskAction(variant: Variant, action: LibraryAGPTaskAction) {
        action.onVariants(
            project,
            androidExtension,
            variant,
            variant.name.capitalize()
        )
    }

    override fun addAGPTaskAction(classicVariant: LibraryVariant, action: LibraryAGPTaskAction) {
        action.onVariants(
            project,
            androidExtension,
            findVariantByClassicVariant(classicVariant),
            classicVariant.name.capitalize()
        )
    }

    override fun <T : LibrarySelfManageableProvider<*>> getProvider(
        classicVariant: LibraryVariant,
        clazz: Class<T>
    ): T {
        return providers.getProvider(
            clazz,
            project,
            androidExtension,
            findVariantByClassicVariant(classicVariant)
        )
    }

    override fun <T : LibrarySelfManageableProvider<*>> getProvider(variant: Variant, clazz: Class<T>): T {
        return providers.getProvider(
            clazz,
            project,
            androidExtension,
            variant
        )
    }

    override fun findVariantByClassicVariant(classicVariant: LibraryVariant): Variant =
        variants.first { v -> v.name == classicVariant.name }


}