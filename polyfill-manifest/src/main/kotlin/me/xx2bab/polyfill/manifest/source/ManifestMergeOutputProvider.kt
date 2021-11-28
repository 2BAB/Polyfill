package me.xx2bab.polyfill.manifest.source

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.matrix.base.ApplicationSelfManageableProvider
import org.gradle.api.Project
import org.gradle.api.file.RegularFile

@Deprecated(
    message = "Use new Variant API instead since it became stable already.",
    replaceWith = ReplaceWith("variant.artifacts.get(SingleArtifact.MERGED_MANIFEST)")
)
class ManifestMergeOutputProvider : ApplicationSelfManageableProvider<RegularFile> {

    private lateinit var mergedManifests: RegularFile

    override fun initialize(
        project: Project,
        androidExtension: AndroidComponentsExtension<*, *, *>,
        variant: Variant
    ) {
        mergedManifests = variant.artifacts.get(SingleArtifact.MERGED_MANIFEST).get()
    }

    override fun obtain(defaultValue: RegularFile?): RegularFile {
        return mergedManifests
    }

}