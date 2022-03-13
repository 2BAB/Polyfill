package me.xx2bab.polyfill.res

import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import com.android.build.gradle.internal.scope.InternalArtifactType
import com.android.build.gradle.internal.tasks.factory.dependsOn
import me.xx2bab.polyfill.agp.toApkCreationConfigImpl
import me.xx2bab.polyfill.agp.toTaskContainer
import me.xx2bab.polyfill.task.SingleArtifactPincerTaskConfiguration
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.withType

/**
 * Configurations for fetching required data and set up dependencies
 * through both explicit/implicit approaches.
 */
class ResourceMergePostHookConfigureAction(
    project: Project,
    variant: Variant,
    headTaskProvider: TaskProvider<*>,
    lazyLastTaskProvider: () -> TaskProvider<*>
) : SingleArtifactPincerTaskConfiguration<Directory>(project, variant, headTaskProvider, lazyLastTaskProvider) {

    override val data: Provider<Directory>
        get() = variant.toApkCreationConfigImpl().config.artifacts.get(InternalArtifactType.MERGED_RES)

    override fun orchestrate() {
        project.afterEvaluate {
            // Left flank
            val mergeTaskProvider = variant.toTaskContainer().mergeResourcesTask
            headTaskProvider.dependsOn(mergeTaskProvider)

            // Right flank
            val linkTaskProvider = tasks.withType<LinkApplicationAndroidResourcesTask>().first {
                it.name.contains(variant.name, true) && !it.name.contains("test", true)
            }
            linkTaskProvider.dependsOn(lazyLastTaskProvider())
        }
    }


}