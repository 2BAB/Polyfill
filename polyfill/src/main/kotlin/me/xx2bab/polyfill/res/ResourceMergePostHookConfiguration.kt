package me.xx2bab.polyfill.res

import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.internal.scope.InternalArtifactType
import me.xx2bab.polyfill.PolyfillAction
import me.xx2bab.polyfill.getApkCreationConfigImpl
import me.xx2bab.polyfill.getTaskContainer
import me.xx2bab.polyfill.task.SingleArtifactTaskExtendConfiguration
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
) : SingleArtifactTaskExtendConfiguration<Directory>(project, appVariant, actionList) {

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
        }
    }


}