package me.xx2bab.polyfill.res

import com.android.build.api.variant.ApplicationVariant
import me.xx2bab.polyfill.PolyfillAction
import me.xx2bab.polyfill.ResourceMergePreHookPatch
import me.xx2bab.polyfill.getTaskContainer
import me.xx2bab.polyfill.task.MultipleArtifactTaskExtendConfiguration
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import java.io.File

/**
 * Configurations for fetching required data and set up dependencies
 * through both explicit/implicit approaches.
 */
class ResourceMergePreHookConfiguration(
    project: Project,
    private val appVariant: ApplicationVariant,
    actionList: () -> List<PolyfillAction<List<Directory>>>
) : MultipleArtifactTaskExtendConfiguration<Directory>(
    project, appVariant, actionList
) {

    override val data: Provider<List<Directory>>
        get() {
            return project.provider {
                val mergeTask = appVariant.getTaskContainer().mergeResourcesTask.get()
                ResourceMergePreHookPatch(mergeTask, project).applyOrDefault {
                    val resourcesComputer = mergeTask.resourcesComputer
                    val resourceSets = resourcesComputer.compute(
                        mergeTask.processResources,
                        null, project.objects.directoryProperty()
                    )
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
        }

    override fun orchestrate() {
        project.afterEvaluate {
            // Right flank
            val mergeTaskProvider = appVariant.getTaskContainer().mergeResourcesTask
            actionList().forEachIndexed { index, action ->
                mergeTaskProvider.configure {
                    action.onTaskConfigure(this)
                    doFirst("ResourceMergePreHookByPolyfill$index") {
                        action.onExecute(data)
                    }
                }
            }
        }
    }


}