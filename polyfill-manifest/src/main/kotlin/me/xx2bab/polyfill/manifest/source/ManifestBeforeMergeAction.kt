package me.xx2bab.polyfill.manifest.source

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.matrix.base.ApplicationAGPTaskAction
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

/**
 * Add a hook entry for "After Manifest Merge".
 */
class ManifestBeforeMergeAction(private val taskProvider: TaskProvider<*>) : ApplicationAGPTaskAction {

    override fun orchestrate(
        project: Project,
        androidExtension: AndroidComponentsExtension<*, *, *>,
        variant: Variant,
        variantCapitalizedName: String
    ) {
        // `variant.toTaskContainer().processManifestTask` can not guarantee the impl class
        project.afterEvaluate {
            project.tasks.named("process${variantCapitalizedName}MainManifest")
                .apply { configure { it.dependsOn(taskProvider) } }
        }
        project.rootProject.subprojects { subProject ->
            if (subProject !== project) {
                subProject.tasks.whenTaskAdded { newTask ->
                    if (newTask.name == "process${variantCapitalizedName}Manifest"
                        || newTask.name == "extractDeepLinks${variantCapitalizedName}") {
                        taskProvider.configure { preUpdateTask ->
                            preUpdateTask.dependsOn(newTask)
                        }
                    }
                }
            }
        }
    }
}