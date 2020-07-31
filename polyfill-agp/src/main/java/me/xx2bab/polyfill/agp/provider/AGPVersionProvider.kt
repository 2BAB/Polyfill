package me.xx2bab.polyfill.agp.provider


import com.android.Version
import me.xx2bab.polyfill.gradle.tool.SemanticVersionLite
import me.xx2bab.polyfill.matrix.annotation.InitStage
import me.xx2bab.polyfill.matrix.annotation.ProviderConfig
import me.xx2bab.polyfill.matrix.base.DataProvider
import org.gradle.api.Project

/**
 * To get the Android Gradle Plugin version that the user is currently using.
 * e.g. 3.6.3, 4.0.0
 *
 * The result will be formatted by [SemanticVersionLite].
 */
@ProviderConfig(InitStage.PRE_BUILD)
class AGPVersionProvider() : DataProvider<SemanticVersionLite> {

    private lateinit var agpVersion: SemanticVersionLite

    override fun initialize(project: Project) {
        agpVersion = SemanticVersionLite(Version.ANDROID_GRADLE_PLUGIN_VERSION)
    }

    override fun get(defaultValue: SemanticVersionLite?): SemanticVersionLite? {
        return agpVersion
    }

    override fun isPresent(): Boolean {
        return true
    }


}