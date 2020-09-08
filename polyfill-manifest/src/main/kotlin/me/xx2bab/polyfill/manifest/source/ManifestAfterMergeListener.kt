package me.xx2bab.polyfill.manifest.source

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.VariantProperties
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.tasks.ProcessApplicationManifest
import me.xx2bab.polyfill.matrix.base.AGPTaskListener
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

/**
 * Add a hook entry for "Before Manifest Merge".
 */
class ManifestAfterMergeListener(private val taskProvider: TaskProvider<*>) : AGPTaskListener {

    override fun onProjectEvaluated(project: Project,
                                    androidExtension: DomainObjectSet<out BaseVariant>) {

    }

    override fun onVariantProperties(project: Project,
                                     androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                                     variant: VariantProperties,
                                     variantCapitalizedName: String) {
//        val manifestUpdater = project.tasks.register("postUpdate${variantCapitalizedName}Manifest",
//                ManifestAfterMergeTask::class.java) {}
//        variant.artifacts.use(manifestUpdater)
//                .wiredWithFiles(ManifestAfterMergeTask::mergedManifest,
//                        ManifestAfterMergeTask::updatedManifest)
//                .toTransform(ArtifactType.MERGED_MANIFEST) // Will update the merge result
    }

    override fun onVariantClassicProperties(project: Project,
                                            androidExtension: BaseExtension,
                                            variant: BaseVariant,
                                            variantCapitalizedName: String) {
        variant.outputs.all {
            project.afterEvaluate {
                project.tasks.withType(ProcessApplicationManifest::class.java)
                        .single {
                            it.variantName.contains(variantCapitalizedName, ignoreCase = true)
                        }
                        .apply {
                            val processApplicationManifest = this
                            taskProvider.configure {
                                processApplicationManifest.dependsOn(it)
                            }
                        }
            }
        }
    }

}