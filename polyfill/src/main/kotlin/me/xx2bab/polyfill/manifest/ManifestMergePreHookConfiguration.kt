package me.xx2bab.polyfill.manifest

import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import me.xx2bab.polyfill.PolyfillAction
import me.xx2bab.polyfill.getCapitalizedName
import me.xx2bab.polyfill.getVariantDependenciesImpl
import me.xx2bab.polyfill.task.MultipleArtifactTaskExtendConfiguration
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import javax.inject.Inject

/**
 * Configurations for fetching required data and set up dependencies
 * through both explicit/implicit approaches.
 */
class ManifestMergePreHookConfiguration(
    project: Project,
    private val appVariant: ApplicationVariant,
    actionList: () -> List<PolyfillAction<List<RegularFile>>>
) : MultipleArtifactTaskExtendConfiguration<RegularFile>
    (project, appVariant, actionList) {

    override val data: Provider<List<RegularFile>> = project.objects.newInstance(
        CreateAction::class.java,
        appVariant.getVariantDependenciesImpl()
            .getArtifactCollection(
                AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                AndroidArtifacts.ArtifactScope.ALL,
                AndroidArtifacts.ArtifactType.MANIFEST
            )
            .resolvedArtifacts
    ).transform()

    override fun orchestrate() {
        // `variant.toTaskContainer().processManifestTask` can not guarantee the impl class
        val variantCapitalizedName = variant.getCapitalizedName()
        project.tasks.whenTaskAdded {
            // > if (this is ProcessApplicationManifest)
            // Can not use above logic since it includes more unwanted tasks
            if (this.name == "process${variantCapitalizedName}MainManifest") {
                // Create a local copy to
                // 1. Avoid referring the *TaskConfiguration class with Project instance
                // 2. Avoid referring any Project instance from task.doFirst()/doLast()
                //  that help us comply the Configuration Cache rules.
                val localData = data
                actionList().forEachIndexed { index, action ->
                    action.onTaskConfigure(this)
                    doFirst("ManifestMergePreHookByPolyfill$index") {
                        action.onExecute(localData)
                    }
                }
            }
        }
    }

    /**
     * To avoid referring project instance directly, we need to create a wrapper,
     * then inject/gather those build services & data into it.
     *
     * @see https://docs.gradle.org/current/userguide/configuration_cache.html#config_cache:requirements:use_project_during_execution
     */
    abstract class CreateAction @Inject constructor(
        private val inputCollection: Provider<Set<ResolvedArtifactResult>>
    ) {

        @get:Inject
        abstract val objectFactory: ObjectFactory

        fun transform(): Provider<List<RegularFile>> {
            return inputCollection.map { set ->
                set.map {
                    val rp = objectFactory.fileProperty()
                    rp.fileValue(it.file)
                    rp.get()
                }
            }
        }
    }

}
