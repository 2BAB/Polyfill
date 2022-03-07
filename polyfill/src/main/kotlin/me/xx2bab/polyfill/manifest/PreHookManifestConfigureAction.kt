package me.xx2bab.polyfill.manifest

import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import me.xx2bab.polyfill.agp.toApkCreationConfigImpl
import me.xx2bab.polyfill.getCapitalizedName
import me.xx2bab.polyfill.task.MultipleArtifactPincerTaskConfiguration
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register

/**
 * Configurations for fetching required data and set up dependencies
 * through both explicit/implicit approaches.
 */
class PreHookManifestConfigureAction(
    project: Project,
    variant: Variant,
    lazyLastTaskProvider: () -> TaskProvider<*>
) : MultipleArtifactPincerTaskConfiguration<RegularFile, PreHookManifestTask>(project, variant, lazyLastTaskProvider) {

    override val initialTaskProvider: TaskProvider<PreHookManifestTask> = project.tasks.register<PreHookManifestTask>(
        "preHookManifestFor${variant.getCapitalizedName()}Task"
    )

    override val from: (PreHookManifestTask) -> ListProperty<RegularFile> = PreHookManifestTask::inputManifests

    override val data: Provider<List<RegularFile>> = variant.toApkCreationConfigImpl()
        .config
        .variantDependencies
        .getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.ALL,
            AndroidArtifacts.ArtifactType.MANIFEST
        )
        .artifactFiles
        .elements
        .map { set ->
            set.map {
                // Default
                val rp = project.objects.fileProperty()
                rp.fileValue(it.asFile)
                rp.get()
            }
        }

    override val tailTaskProviders: List<TaskProvider<*>> = listOf()

    override fun orchestrate(): Pair<TaskProvider<*>, Provider<List<RegularFile>>> {
        // `variant.toTaskContainer().processManifestTask` can not guarantee the impl class
        val variantCapitalizedName = variant.getCapitalizedName()
        project.tasks.whenTaskAdded {
            val targetTask = this
            if (this.name == "process${variantCapitalizedName}MainManifest") {
                targetTask.dependsOn(lazyLastTaskProvider())
            }
        }
        project.rootProject.subprojects {
            val subProject = this
            if (subProject !== project) {
                subProject.tasks.whenTaskAdded {
                    val targetTask = this
                    if (targetTask.name == "process${variantCapitalizedName}Manifest"
                        || targetTask.name == "extractDeepLinks${variantCapitalizedName}"
                    ) {
                        lazyLastTaskProvider().configure {
                            dependsOn(targetTask)
                        }
                    }
                }
            }
        }
        return super.orchestrate()
    }


}