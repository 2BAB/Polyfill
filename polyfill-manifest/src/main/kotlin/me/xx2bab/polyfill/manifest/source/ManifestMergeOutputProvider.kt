package me.xx2bab.polyfill.manifest.source

import com.android.build.api.artifact.ArtifactType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.VariantProperties
import com.android.build.gradle.api.BaseVariant
import me.xx2bab.polyfill.matrix.base.SelfManageableProvider
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

class ManifestMergeOutputProvider: SelfManageableProvider<RegularFile> {

    private lateinit var mergedManifests: Provider<RegularFile>

    override fun initialize(project: Project,
                            androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                            variantProperties: VariantProperties,
                            variantClassicProperties: BaseVariant) {
        mergedManifests = variantProperties.artifacts.get(ArtifactType.MERGED_MANIFEST)
    }

    override fun get(defaultValue: RegularFile?): RegularFile? {
        return mergedManifests.get()
    }

    override fun isPresent(): Boolean {
        return ::mergedManifests.isInitialized && mergedManifests.isPresent
    }

}