package me.xx2bab.polyfill.res

import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.DependencyResourcesComputer
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.android.build.gradle.tasks.MergeResources
import me.xx2bab.polyfill.agp.toTaskContainer
import me.xx2bab.polyfill.task.MultipleArtifactPincerTaskConfiguration
import me.xx2bab.polyfill.tools.ReflectionKit
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import java.io.File

/**
 * Configurations for fetching required data and set up dependencies
 * through both explicit/implicit approaches.
 */
class ResourceMergePreHookConfigureAction(
    project: Project,
    variant: Variant,
    headTaskProvider: TaskProvider<*>,
    lazyLastTaskProvider: () -> TaskProvider<*>
) : MultipleArtifactPincerTaskConfiguration<Directory>(
    project, variant, headTaskProvider, lazyLastTaskProvider
) {

    override val data: Provider<List<Directory>>
        get() {
            return project.provider {
                println("ResourceMergePreHookConfigureAction provider")
                val mergeTask = variant.toTaskContainer().mergeResourcesTask.get()
                val resourcesComputer = ReflectionKit.getField(
                    MergeResources::class.java,
                    mergeTask,
                    "resourcesComputer"
                ) as DependencyResourcesComputer
                val resourceSets = resourcesComputer.compute(mergeTask.processResources, null)
                val resourceFiles = resourceSets.mapNotNull { resourceSet ->
                    val getSourceFiles = resourceSet.javaClass.methods.find {
                        it.name == "getSourceFiles" && it.parameterCount == 0
                    }
                    @Suppress("UNCHECKED_CAST")
                    getSourceFiles?.invoke(resourceSet) as? Iterable<File>
                }.flatten()
                resourceFiles.map { file ->
                    // A hacky way to transform File -> RegularFile
                    val rp = project.objects.directoryProperty()
                    rp.fileValue(file)
                    rp.get()
                }
            }
        }

    override fun orchestrate() {
        project.afterEvaluate {
            // Right flank
            variant.toTaskContainer().mergeResourcesTask.dependsOn(lazyLastTaskProvider())
        }
    }


}