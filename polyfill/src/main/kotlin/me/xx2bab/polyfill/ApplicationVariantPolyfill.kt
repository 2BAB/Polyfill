package me.xx2bab.polyfill

import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.matrix.base.ApplicationAGPTaskAction
import me.xx2bab.polyfill.matrix.base.ApplicationSelfManageableProvider
import org.gradle.api.Project

class ApplicationVariantPolyfill(project: Project, variant: Variant) : Polyfill<
        ApplicationAGPTaskAction,
        ApplicationSelfManageableProvider<*>>(project, variant) {

    override fun addAGPTaskAction(action: ApplicationAGPTaskAction) {
        action.orchestrate(
            project, androidExtension, variant, variant.name.capitalize()
        )
    }

    override fun <T : ApplicationSelfManageableProvider<*>> newProvider(clazz: Class<T>): T {
        val instance = clazz.getDeclaredConstructor().newInstance()
        instance.initialize(project, androidExtension, variant)
        return instance
    }

}