package me.xx2bab.polyfill

import com.android.build.api.extension.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Action
import org.gradle.api.Project

typealias androidExt = AndroidComponentsExtension<*, *>

/**
 * The entry for overall API calls. Polyfill itself doesn't allow instance constructing,
 * should always create its subclasses via [createApplicationPolyfill] or [createLibraryPolyfill],
 * from [PolyfillFactory], to support their own [BaseVariant] [AGPTaskAction] respectively.
 */
abstract class Polyfill<ClassicVariant, AGPTaskAction, SelfManageableProvider> internal constructor(val project: Project) {

    protected val providers = ProviderCache()
    protected val androidExtension: androidExt
    protected val variants = mutableSetOf<Variant>()

    init {
        if (project.extensions.findByType(AndroidComponentsExtension::class.java) == null) {
            throw IllegalArgumentException("Required Application or Library Extensions.")
        } else {
            androidExtension = project.extensions.findByType(AndroidComponentsExtension::class.java)!!
            androidExtension.onVariants { v -> variants.add(v) }
        }
    }

    fun onVariants(action: Action<Variant>) {
        androidExtension.onVariants { variant ->
            action.execute(variant)
        }
    }

    abstract fun onClassicVariants(action: Action<ClassicVariant>)

    abstract fun addAGPTaskAction(variant: Variant, action: AGPTaskAction)

    abstract fun addAGPTaskAction(classicVariant: ClassicVariant, action: AGPTaskAction)

    abstract fun <T : SelfManageableProvider> getProvider(variant: Variant, clazz: Class<T>): T

    abstract fun <T : SelfManageableProvider> getProvider(classicVariant: ClassicVariant, clazz: Class<T>): T

    abstract fun findVariantByClassicVariant(classicVariant: ClassicVariant): Variant

    class UnsupportedAGPVersionException(msg: String) : Exception(msg)
}
