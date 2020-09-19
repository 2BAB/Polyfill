package me.xx2bab.polyfill

import com.android.build.api.variant.VariantProperties
import me.xx2bab.polyfill.agp.provider.AGPVersionProvider
import me.xx2bab.polyfill.agp.provider.BuildToolProvider
import me.xx2bab.polyfill.manifest.bytes.ManifestInBytesProvider
import me.xx2bab.polyfill.manifest.source.ManifestMergeInputProvider
import me.xx2bab.polyfill.manifest.source.ManifestMergeOutputProvider
import me.xx2bab.polyfill.matrix.base.SelfManageableProvider
import org.gradle.api.Project
import java.util.concurrent.ConcurrentHashMap

class ProviderCache {

    private val variantMap = ConcurrentHashMap<String,
            MutableMap<Class<out SelfManageableProvider<*>>, SelfManageableProvider<*>>>()

    // TODO: generate this helper function automatically during runtime
    @Suppress("UNCHECKED_CAST")
    private fun <T : SelfManageableProvider<*>> newProviderInstance(clazz: Class<T>): T {
        return when (clazz) {
            AGPVersionProvider::class.java -> AGPVersionProvider() as T
            BuildToolProvider::class.java -> BuildToolProvider() as T
            ManifestInBytesProvider::class.java -> ManifestInBytesProvider() as T
            ManifestMergeInputProvider::class.java -> ManifestMergeInputProvider() as T
            ManifestMergeOutputProvider::class.java -> ManifestMergeOutputProvider() as T
            else -> throw IllegalArgumentException("${clazz.canonicalName} is not found.")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : SelfManageableProvider<*>> getProvider(clazz: Class<T>,
                                                    project: Project,
                                                    androidExtension: androidExt,
                                                    variant: VariantProperties): T {
        val variantName = variant.name
        if (!variantMap.containsKey(variantName)) {
            variantMap[variantName] = ConcurrentHashMap()
        }
        val providers = variantMap[variantName]!!
        return if (providers.containsKey(clazz)) {
            providers[clazz] as T
        } else {
            val instance = newProviderInstance(clazz)
            instance.initialize(project, androidExtension, variant)
            providers[clazz] = instance
            instance
        }
    }

}