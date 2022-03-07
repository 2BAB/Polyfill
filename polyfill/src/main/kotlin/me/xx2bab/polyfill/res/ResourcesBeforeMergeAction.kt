//package me.xx2bab.polyfill.res
//
//import com.android.build.api.variant.AndroidComponentsExtension
//import com.android.build.api.variant.Variant
//import me.xx2bab.polyfill.agp.toTaskContainer
//import org.gradle.api.Project
//import org.gradle.api.tasks.TaskProvider
//
//class ResourcesBeforeMergeAction(private val taskProvider: TaskProvider<*>) {
//
//    override fun orchestrate(
//        project: Project,
//        androidExtension: AndroidComponentsExtension<*, *, *>,
//        variant: Variant,
//        variantCapitalizedName: String
//    ) {
//        project.afterEvaluate {
//            // Abuse of finalizedBy(...)
//            val mergeTaskProvider = variant.toTaskContainer().mergeResourcesTask
//            mergeTaskProvider.configure { finalizedBy(taskProvider) }
//        }
//    }
//
//}