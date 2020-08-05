package me.xx2bab.polyfill

import me.xx2bab.polyfill.arsc.base.ResTable
import me.xx2bab.polyfill.arsc.export.IResArscTweaker
import me.xx2bab.polyfill.manifest.bytes.parser.IManifestBytesTweaker
import me.xx2bab.polyfill.manifest.bytes.parser.ManifestBytesTweaker
import me.xx2bab.polyfill.matrix.base.AGPTaskListener
import org.gradle.api.Project


class Polyfill(val project: Project) {

    private val arscTweaker: IResArscTweaker = ResTable()
    private val manifestBytesTweaker: IManifestBytesTweaker = ManifestBytesTweaker()

    private val serviceMap = mapOf<Class<*>, Class<*>>(
            IResArscTweaker::class.java to ResTable::class.java)

    init {
        if (!checkSupportedGradleVersion()) {
            throw UnsupportedAGPVersionException("Required minimum Android Gradle Plugin version is: ")
        }
    }

    private fun checkSupportedGradleVersion(): Boolean {
        return true
    }

    fun <T> getService(apiInterface: Class<T>): T? {
        val impl = serviceMap[apiInterface] ?: return null
        return impl.newInstance() as T
    }

    fun addOnAGPTaskListener(listener: AGPTaskListener) {

    }

    class UnsupportedAGPVersionException(msg: String): Exception(msg)

}
