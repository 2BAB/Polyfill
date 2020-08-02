package me.xx2bab.polyfill.agp.provider


import com.android.Version
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.VariantProperties
import com.android.build.gradle.api.BaseVariant
import me.xx2bab.polyfill.gradle.tool.SemanticVersionLite
import me.xx2bab.polyfill.matrix.annotation.InitStage
import me.xx2bab.polyfill.matrix.annotation.ProviderConfig
import me.xx2bab.polyfill.matrix.base.SelfManageableProvider
import org.gradle.api.Project

/**
 * To get the Android Gradle Plugin version that the user is currently using.
 * e.g. 3.6.3, 4.0.0
 *
 * The result will be formatted by [SemanticVersionLite].
 */
@ProviderConfig(InitStage.PRE_BUILD)
class AGPVersionProvider() : SelfManageableProvider<SemanticVersionLite> {

    private var agpVersion: SemanticVersionLite = SemanticVersionLite(Version.ANDROID_GRADLE_PLUGIN_VERSION)

    override fun initialize(project: Project,
                            androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                            variantProperties: VariantProperties,
                            variantClassicProperties: BaseVariant) {
        // Could be ignored
    }

    override fun get(defaultValue: SemanticVersionLite?): SemanticVersionLite? {
        return agpVersion
    }

    override fun isPresent(): Boolean {
        return true
    }



}