package me.xx2bab.polyfill

import com.android.build.api.variant.Variant
import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import me.xx2bab.polyfill.matrix.base.ApplicationAGPTaskAction
import me.xx2bab.polyfill.matrix.base.ApplicationSelfManageableProvider
import org.gradle.api.Action
import org.gradle.api.Project

class ApplicationPolyfill(project: Project) : Polyfill<ApplicationVariant,
        ApplicationAGPTaskAction,
        ApplicationSelfManageableProvider<*>>(project) {

    private val appClassicExt = project.extensions.findByType(AppExtension::class.java)!!

    override fun onClassicVariants(action: Action<ApplicationVariant>) {
        appClassicExt.applicationVariants.all { v ->
            action.execute(v)
        }
    }

    override fun addAGPTaskAction(variant: Variant, action: ApplicationAGPTaskAction) {
        action.onVariants(
            project,
            androidExtension,
            variant,
            variant.name.capitalize()
        )
    }

    override fun addAGPTaskAction(classicVariant: ApplicationVariant, action: ApplicationAGPTaskAction) {
        val variant = variants.first { classicVariant.name == it.name }
        action.onVariants(
            project,
            androidExtension,
            variant,
            variant.name.capitalize()
        )
    }

    override fun <T : ApplicationSelfManageableProvider<*>> getProvider(
        variant: Variant,
        clazz: Class<T>
    ): T {
        return providers.getProvider(
            clazz,
            project,
            androidExtension,
            variant
        )
    }

    override fun <T : ApplicationSelfManageableProvider<*>> getProvider(
        classicVariant: ApplicationVariant,
        clazz: Class<T>
    ): T {
        return providers.getProvider(
            clazz,
            project,
            androidExtension,
            findVariantByClassicVariant(classicVariant)
        )
    }

    override fun findVariantByClassicVariant(classicVariant: ApplicationVariant): Variant =
        variants.first { v -> v.name == classicVariant.name }

}