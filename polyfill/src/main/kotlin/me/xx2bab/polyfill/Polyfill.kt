package me.xx2bab.polyfill

import com.android.Version
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.api.AndroidBasePlugin
import me.xx2bab.polyfill.gradle.tool.SemanticVersionLite
import me.xx2bab.polyfill.matrix.base.SelfManageableProvider
import org.gradle.api.Project

typealias androidExt = AndroidComponentsExtension<*, *, *>

/**
 * The entry for overall API calls. Polyfill itself doesn't allow instance constructing,
 * please using its subclasses [ApplicationVariantPolyfill] or [LibraryVariantPolyfill].
 */
abstract class Polyfill<AGPTaskAction, Provider: SelfManageableProvider<*>> internal constructor(
    val project: Project,
    val variant: Variant
) {

    protected val androidExtension: androidExt

    init {
        checkAndroidPlugin()
        checkSupportedGradleVersion()

        androidExtension = project.extensions.findByType(AndroidComponentsExtension::class.java)!!
    }

    abstract fun addAGPTaskAction(action: AGPTaskAction)

    abstract fun <T : Provider> newProvider(clazz: Class<T>): T

    private fun checkAndroidPlugin() {
        if (!project.plugins.hasPlugin(AndroidBasePlugin::class.java)) {
            throw UnsupportedAGPVersionException("Android Application or Library plugin required.")
        }
        if (project.extensions.findByType(AndroidComponentsExtension::class.java) == null) {
            throw IllegalArgumentException("Required Application or Library Extensions.")
        }
    }
    private fun checkSupportedGradleVersion() {
        val curr = SemanticVersionLite(Version.ANDROID_GRADLE_PLUGIN_VERSION)
        val min = SemanticVersionLite("7.0")
        val max = SemanticVersionLite("7.2")
        if (!(curr >= min && curr < max)) {
            throw throw UnsupportedAGPVersionException("Required minimum Android Gradle Plugin version ")
        }
    }

    class UnsupportedAGPVersionException(msg: String) : Exception(msg)
}
