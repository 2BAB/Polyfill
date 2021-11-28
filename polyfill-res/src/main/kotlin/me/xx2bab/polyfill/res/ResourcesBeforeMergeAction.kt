package me.xx2bab.polyfill.res

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.agp.tool.toTaskContainer
import me.xx2bab.polyfill.matrix.base.ApplicationAGPTaskAction
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

class ResourcesBeforeMergeAction(private val taskProvider: TaskProvider<*>) : ApplicationAGPTaskAction {

    override fun orchestrate(
        project: Project,
        androidExtension: AndroidComponentsExtension<*, *, *>,
        variant: Variant,
        variantCapitalizedName: String
    ) {
        project.afterEvaluate {
            val mergeTask = variant.toTaskContainer().mergeResourcesTask.get()
            mergeTask.dependsOn(taskProvider)
        }
    }

}