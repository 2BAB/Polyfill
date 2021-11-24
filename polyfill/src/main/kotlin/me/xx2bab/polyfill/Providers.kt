package me.xx2bab.polyfill

import me.xx2bab.polyfill.agp.provider.AGPVersionProvider
import me.xx2bab.polyfill.agp.provider.BuildToolProvider
import me.xx2bab.polyfill.manifest.bytes.ManifestInBytesProvider
import me.xx2bab.polyfill.manifest.source.ManifestMergeInputProvider
import me.xx2bab.polyfill.manifest.source.ManifestMergeOutputProvider
import me.xx2bab.polyfill.matrix.base.SelfManageableProvider

class Providers {

    // TODO: generate this helper function automatically during runtime
    @Suppress("UNCHECKED_CAST")
    fun <T : SelfManageableProvider<*>> newProviderInstance(clazz: Class<T>): T {
        return when (clazz) {
            AGPVersionProvider::class.java -> AGPVersionProvider() as T
            BuildToolProvider::class.java -> BuildToolProvider() as T
            ManifestInBytesProvider::class.java -> ManifestInBytesProvider() as T
            ManifestMergeInputProvider::class.java -> ManifestMergeInputProvider() as T
            ManifestMergeOutputProvider::class.java -> ManifestMergeOutputProvider() as T
            else -> throw IllegalArgumentException("${clazz.canonicalName} is not found.")
        }
    }

}