package me.xx2bab.polyfill.manifest.source

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.tasks.ProcessLibraryManifest
import me.xx2bab.polyfill.matrix.base.ApplicationAGPTaskListener
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

/**
 * Add a hook entry for "After Manifest Merge".
 */
class ManifestBeforeMergeListener(private val taskProvider: TaskProvider<*>) : ApplicationAGPTaskListener {

    override fun onVariantProperties(project: Project,
                                     androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                                     variant: Variant,
                                     variantCapitalizedName: String) {
        project.afterEvaluate {
            project.tasks.named("process${variantCapitalizedName}MainManifest")
                    .apply { configure { it.dependsOn(taskProvider) } }
            project.rootProject.subprojects { subProject ->
                if (subProject !== project) {
                    taskProvider.configure { task ->
                        // TODO: check if :library:extractDeepLinksDebug is also required to be dependency
                        val tasks = subProject.tasks.withType(ProcessLibraryManifest::class.java)
                        if (tasks.size > 0) {
                            tasks.configureEach { task.dependsOn(it) }
                        }
                    }
                }
            }
        }
    }
}