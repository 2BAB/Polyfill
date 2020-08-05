package me.xx2bab.polyfill.manifest.source

import com.android.build.api.artifact.ArtifactType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.VariantProperties
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.tasks.ProcessApplicationManifest
import me.xx2bab.polyfill.matrix.base.AGPTaskListener
import org.gradle.api.Project
import java.io.File

abstract class ManifestMergeTaskListener : AGPTaskListener {

    private lateinit var beforeMergeAction: (Collection<File>) -> Unit
    private lateinit var afterMergeAction: (File) -> ByteArray

    override fun onVariantProperties(project: Project,
                                     androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                                     variant: VariantProperties,
                                     variantCapitalizedName: String) {
        val manifestUpdater = project.tasks.register("postUpdate${variantCapitalizedName}Manifest",
                ManifestAfterMergeTask::class.java) {
            it.afterMergeAction = afterMergeAction
        }
        variant.artifacts.use(manifestUpdater)
                .wiredWithFiles(ManifestAfterMergeTask::mergedManifest,
                        ManifestAfterMergeTask::updatedManifest)
                .toTransform(ArtifactType.MERGED_MANIFEST) // Will update the merge result
    }

    override fun onVariantClassicProperties(project: Project,
                                            androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                                            variant: BaseVariant,
                                            variantCapitalizedName: String) {
        variant.outputs.all {
            val processAppManifestTask = it.processManifestProvider.get() as ProcessApplicationManifest

            processAppManifestTask.doFirst ("preUpdate${variantCapitalizedName}Manifest"){
                beforeMergeAction.invoke(processAppManifestTask.getManifests().files)
            }

            // Deprecated, since we prefer to use the AGP public api if it works well.
            // Check the onVariantProperties process above.
            /*processAppManifestTask.doLast ("postUpdate${variantCapitalizedName}Manifest"){
                afterMergeAction.execute(processAppManifestTask.mergedManifest.get().asFile)
                // ... To handle the merged manifest content update
            }*/
        }
    }

    /**
     * Add a hook entry for "Before Manifest Merge".
     *
     * @param action The function accepts a collection of `AndroidManifest.xml` that will be merged,
     * and can process those files in place (without changing the file location).
     */
    fun beforeMerge(action: (Collection<File>) -> Unit) {
        this.beforeMergeAction = action
    }

    /**
     * Add a hook entry for "After Manifest Merge".
     *
     * @param action The function accepts the merged `AndroidManifest.xml` file, and can process
     * transformation based on it. It should eventually returns the transformed result in ByteArray.
     */
    fun afterMerge(action: (File) -> ByteArray) {
        this.afterMergeAction = action
    }

}