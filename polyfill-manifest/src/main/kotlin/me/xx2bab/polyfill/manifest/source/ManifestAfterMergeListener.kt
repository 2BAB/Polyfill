package me.xx2bab.polyfill.manifest.source

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.VariantProperties
import com.android.build.gradle.tasks.ProcessApplicationManifest
import me.xx2bab.polyfill.matrix.base.AGPTaskListener
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

/**
 * Add a hook entry for "Before Manifest Merge".
 */
class ManifestAfterMergeListener(private val taskProvider: TaskProvider<*>) : AGPTaskListener {

    override fun onVariantProperties(project: Project,
                                     androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                                     variant: VariantProperties,
                                     variantCapitalizedName: String) {
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