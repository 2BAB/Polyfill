package me.xx2bab.polyfill.agp.provider

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.sdklib.BuildToolInfo
import me.xx2bab.polyfill.agp.tool.toGlobalScope
import me.xx2bab.polyfill.matrix.annotation.InitStage
import me.xx2bab.polyfill.matrix.annotation.ProviderConfig
import me.xx2bab.polyfill.matrix.base.ApplicationSelfManageableProvider
import me.xx2bab.polyfill.matrix.base.LibrarySelfManageableProvider
import org.gradle.api.Project
import org.gradle.api.provider.Provider

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
class BuildToolInfoProvider : ApplicationSelfManageableProvider<Provider<BuildToolInfo>>,
        LibrarySelfManageableProvider<Provider<BuildToolInfo>> {

    private lateinit var bti: Provider<BuildToolInfo>

    override fun initialize(project: Project,
                            androidExtension: AndroidComponentsExtension<*, *, *>,
                            variant: Variant) {
        bti = variant.toGlobalScope().versionedSdkLoader.flatMap { it.buildToolInfoProvider }
    }

    override fun obtain(defaultValue: Provider<BuildToolInfo>?): Provider<BuildToolInfo> {
        return bti
    }

}