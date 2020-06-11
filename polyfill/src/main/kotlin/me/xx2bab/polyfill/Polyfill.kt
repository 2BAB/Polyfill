package me.xx2bab.polyfill

import me.xx2bab.polyfill.arsc.base.ResTable
import me.xx2bab.polyfill.arsc.export.IResArscTweaker
import me.xx2bab.polyfill.di.mainModule
import me.xx2bab.polyfill.manifest.post.IManifestPostTweaker
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject

class Polyfill: KoinComponent {

    private val arscTweaker: IResArscTweaker by inject()
    private val manifestPostTweaker: IManifestPostTweaker by inject()

    private val serviceMap = mapOf<Class<*>, Class<*>>(
            IResArscTweaker::class.java to ResTable::class.java)

    init {
        if (!checkSupportedGradleVersion()) {
            throw UnsupportedAGPVersionException("Required minimum Android Gradle Plugin version is: ")
        }

        startKoin {
            modules(mainModule)
        }
    }

    private fun checkSupportedGradleVersion(): Boolean {
        return true
    }

    fun <T> getService(apiInterface: Class<T>): T? {
        val impl = serviceMap[apiInterface] ?: return null
        return impl.newInstance() as T
    }

    class UnsupportedAGPVersionException(msg: String): Exception(msg)

}
