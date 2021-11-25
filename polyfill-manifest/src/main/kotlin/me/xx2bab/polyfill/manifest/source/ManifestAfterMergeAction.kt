package me.xx2bab.polyfill.manifest.source

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.tasks.ProcessApplicationManifest
import me.xx2bab.polyfill.matrix.base.ApplicationAGPTaskAction
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

/**
 * Add a hook entry for "Before Manifest Merge".
 */
@Deprecated(
    message = "Use new Variant API instead since it became stable already.",
    replaceWith = ReplaceWith(
        "variant.artifacts.use(manifestUpdater)\n" +
                "            .wiredWithFiles(\n" +
                "                ManifestTransformerTask::mergedManifest,\n" +
                "                ManifestTransformerTask::updatedManifest)\n" +
                "            .toTransform(com.android.build.api.artifact.SingleArtifact.MERGED_MANIFEST)",
    )
)
class ManifestAfterMergeAction(private val taskProvider: TaskProvider<*>) : ApplicationAGPTaskAction {

    override fun orchestrate(
        project: Project,
        androidExtension: AndroidComponentsExtension<*, *, *>,
        variant: Variant,
        variantCapitalizedName: String
    ) {
        project.afterEvaluate {
            project.tasks.withType(ProcessApplicationManifest::class.java)
                .single {
                    it.variantName.contains(variantCapitalizedName, ignoreCase = true)
                }
                .apply {
                    val processApplicationManifest = this
                    processApplicationManifest.finalizedBy(taskProvider)
                    taskProvider.configure {
                        it.dependsOn(processApplicationManifest)
                        it.mustRunAfter(processApplicationManifest)
                    }
                }
        }
    }

}