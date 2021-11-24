package me.xx2bab.polyfill.manifest.source

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.matrix.base.ApplicationSelfManageableProvider
import org.gradle.api.Project
import org.gradle.api.file.RegularFile

class ManifestMergeOutputProvider: ApplicationSelfManageableProvider<RegularFile> {

    private lateinit var mergedManifests: RegularFile

    override fun initialize(project: Project,
                            androidExtension: AndroidComponentsExtension<*, *, *>,
                            variant: Variant) {
        mergedManifests = variant.artifacts.get(SingleArtifact.MERGED_MANIFEST).get()
//        val t = project.tasks.withType(ProcessApplicationManifest::class.java)
//        mergedManifests = t.first().mergedManifest.get()
    }

    override fun get(defaultValue: RegularFile?): RegularFile? {
        return mergedManifests
    }

    override fun isPresent(): Boolean {
        return ::mergedManifests.isInitialized
    }

}