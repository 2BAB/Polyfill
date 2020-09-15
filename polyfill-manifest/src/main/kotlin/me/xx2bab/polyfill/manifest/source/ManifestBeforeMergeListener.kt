package me.xx2bab.polyfill.manifest.source

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.VariantProperties
import me.xx2bab.polyfill.matrix.base.AGPTaskListener
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

/**
 * Add a hook entry for "After Manifest Merge".
 */
class ManifestBeforeMergeListener(private val taskProvider: TaskProvider<*>) : AGPTaskListener {

    override fun onVariantProperties(project: Project,
                                     androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                                     variant: VariantProperties,
                                     variantCapitalizedName: String) {
        project.afterEvaluate {
            project.tasks.named("process${variantCapitalizedName}MainManifest")
                    .apply { configure { it.dependsOn(taskProvider) } }
        }
    }
}