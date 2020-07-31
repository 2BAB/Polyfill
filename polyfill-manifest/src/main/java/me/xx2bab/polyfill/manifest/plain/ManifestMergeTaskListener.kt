package me.xx2bab.polyfill.manifest.plain

import com.android.build.api.artifact.ArtifactType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.VariantProperties
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.tasks.ProcessApplicationManifest
import me.xx2bab.polyfill.matrix.base.AGPTaskListener
import org.gradle.api.Action
import org.gradle.api.Project
import java.io.File

abstract class ManifestMergeTaskListener : AGPTaskListener {

    private lateinit var beforeMergeAction: Action<Collection<File>>
    private lateinit var afterMergeAction: Action<File>

    override fun onVariantProperties(project: Project,
                                     androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                                     variant: VariantProperties,
                                     variantCapitalizedName: String) {
        // Todo: Add merged manifest to provider
//        val mergedManifestProvider = variant.artifacts.get(ArtifactType.MERGED_MANIFEST)

        val manifestUpdater = project.tasks.register("postUpdate${variantCapitalizedName}Manifest",
                ManifestAfterMergeTask::class.java) {
            // Todo: add action to task
//                    it.gitInfoFile.set(gitVersionProvider.flatMap(GitVersionTask::gitVersionOutputFile))
        }
        variant.artifacts.use(manifestUpdater)
                .wiredWithFiles(ManifestAfterMergeTask::mergedManifest,
                        ManifestAfterMergeTask::updatedManifest)
                .toTransform(ArtifactType.MERGED_MANIFEST)

    }

    override fun onVariantClassicProperties(project: Project,
                                            androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                                            variant: BaseVariant,
                                            variantCapitalizedName: String) {
        variant.outputs.all {
            val processAppManifestTask = it.processManifestProvider.get() as ProcessApplicationManifest

            processAppManifestTask.doFirst ("preUpdate${variantCapitalizedName}Manifest"){
                beforeMergeAction.execute(processAppManifestTask.getManifests().files)
            }

            // Deprecated, since we prefer to use the AGP public api if it works well.
            // Check the onVariantProperties process above.
//            processAppManifestTask.doLast ("postUpdate${variantCapitalizedName}Manifest"){
//                afterMergeAction.execute(processAppManifestTask.mergedManifest.get().asFile)
//                // ... To handle the merged manifest content update
//            }
        }
    }

    fun beforeMerge(action: Action<Collection<File>>) {

    }

    fun afterMerge(action: Action<File>) {

    }

}