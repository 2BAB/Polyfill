package me.xx2bab.polyfill.manifest

import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import me.xx2bab.polyfill.getApkCreationConfigImpl
import me.xx2bab.polyfill.getCapitalizedName
import me.xx2bab.polyfill.task.MultipleArtifactPincerTaskConfiguration
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * Configurations for fetching required data and set up dependencies
 * through both explicit/implicit approaches.
 */
class ManifestMergePreHookConfigureAction(
    project: Project,
    private val appVariant: ApplicationVariant,
    headTaskProvider: TaskProvider<*>,
    lazyLastTaskProvider: () -> TaskProvider<*>?
) : MultipleArtifactPincerTaskConfiguration<RegularFile>
    (project, appVariant, headTaskProvider, lazyLastTaskProvider) {

    override val data: Provider<List<RegularFile>> = appVariant.getApkCreationConfigImpl()
        .config
        .variantDependencies
        .getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.ALL,
            AndroidArtifacts.ArtifactType.MANIFEST
        )
        .resolvedArtifacts
        .map { set ->
            set.map {
                val rp = project.objects.fileProperty()
                rp.fileValue(it.file)
                rp.get()
            }
        }

    override fun orchestrate() {
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
                        headTaskProvider.configure {
                            dependsOn(targetTask)
                        }
                    }
                }
            }
        }
    }

}