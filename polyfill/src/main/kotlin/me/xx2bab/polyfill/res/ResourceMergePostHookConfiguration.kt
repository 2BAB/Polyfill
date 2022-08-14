package me.xx2bab.polyfill.res

import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.internal.scope.InternalArtifactType
import me.xx2bab.polyfill.PolyfillAction
import me.xx2bab.polyfill.getApkCreationConfigImpl
import me.xx2bab.polyfill.getTaskContainer
import me.xx2bab.polyfill.task.SingleArtifactPincerTaskConfiguration
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

/**
 * Configurations for fetching required data and set up dependencies
 * through both explicit/implicit approaches.
 */
class ResourceMergePostHookConfiguration(
    project: Project,
    private val appVariant: ApplicationVariant,
    actionList: () -> List<PolyfillAction<Directory>>
) : SingleArtifactPincerTaskConfiguration<Directory>(project, appVariant, actionList) {

    override val data: Provider<Directory>
        get() = appVariant.getApkCreationConfigImpl().config.artifacts.get(InternalArtifactType.MERGED_RES)

    override fun orchestrate() {
        project.afterEvaluate {
            val mergeTaskProvider = appVariant.getTaskContainer().mergeResourcesTask
            actionList().forEachIndexed { index, action ->
                mergeTaskProvider.configure {
                    action.onTaskConfigure(this)
                    doLast("ResourceMergePostHookByPolyfill$index") {
                        action.onExecute(data)
                    }
                }
            }
//            // Left flank
//            val mergeTaskProvider = appVariant.getTaskContainer().mergeResourcesTask
//            headTaskProvider.dependsOn(mergeTaskProvider)
//
//            // Right flank
//            val linkTaskProvider = tasks.withType<LinkApplicationAndroidResourcesTask>().first {
//                it.name.contains(variant.name, true) && !it.name.contains("test", true)
//            }
//            linkTaskProvider.dependsOn(lazyTailTaskProvider())
        }
    }


}