package me.xx2bab.polyfill.agp.provider

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.scope.GlobalScope
import com.android.sdklib.BuildToolInfo
import me.xx2bab.polyfill.matrix.annotation.InitStage
import me.xx2bab.polyfill.matrix.annotation.ProviderConfig
import me.xx2bab.polyfill.matrix.base.ApplicationSelfManageableProvider
import me.xx2bab.polyfill.matrix.base.LibrarySelfManageableProvider
import me.xx2bab.polyfill.matrix.tool.ReflectionKit
import org.gradle.api.Project

/**
 * To get the BuildTool obj, which is located at sdk dir like:
 *     - ~/Library/Android/sdk/build-tools/29.0.3/ (by macOS)
 *
 * To get the executable tools can refer from constants:
 *     - buildToolInfo.getPath(BuildToolInfo.PathId.AAPT2)
 *     - buildToolInfo.getPath(BuildToolInfo.PathId.DX)
 *     - buildToolInfo.getPath(BuildToolInfo.PathId.ZIP_ALIGN)
 *     - ...
 *
 * @see BuildToolInfo
 */
@ProviderConfig(InitStage.PRE_BUILD)
class BuildToolProvider : ApplicationSelfManageableProvider<BuildToolInfo>,
        LibrarySelfManageableProvider<BuildToolInfo> {

    private lateinit var bti: BuildToolInfo

    override fun initialize(project: Project,
                            androidExtension: AndroidComponentsExtension<*, *, *>,
                            variant: Variant) {
        val basePlugin = project.plugins.findPlugin(AppPlugin::class.java)
        val scope = ReflectionKit.getField(AppPlugin::class.java, basePlugin!!,
                "globalScope") as GlobalScope
        bti = scope.versionedSdkLoader.get().buildToolInfoProvider.get()
    }

    override fun obtain(defaultValue: BuildToolInfo?): BuildToolInfo {
        return bti
    }

}