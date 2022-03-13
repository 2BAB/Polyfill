package me.xx2bab.polyfill.artifact

import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.PolyfillExtension
import me.xx2bab.polyfill.PolyfilledArtifact
import me.xx2bab.polyfill.getCapitalizedName
import me.xx2bab.polyfill.task.PincerTaskConfiguration
import org.gradle.api.Project
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

abstract class ArtifactContainer<CreationData>(
    private val artifactType: PolyfilledArtifact<*>,
    private val project: Project,
    private val variant: Variant,
    private val map: Map<PolyfilledArtifact<*>, KClass<out PincerTaskConfiguration<*>>>
) {

    private val headTaskProvider: TaskProvider<*> = project.tasks.register(
        "dummyHeadWith${artifactType::class.simpleName}For${variant.getCapitalizedName()}"
    )
    private var latestTaskProvider: TaskProvider<*> = headTaskProvider
    private val pincerTaskConfig: PincerTaskConfiguration<CreationData>

    init {
        val configureAction = map[artifactType]!! as (KClass<out PincerTaskConfiguration<CreationData>>)
        pincerTaskConfig = configureAction.constructors.first().call(
            project, variant, headTaskProvider, { latestTaskProvider }
        )
        pincerTaskConfig.orchestrate()
    }

    internal fun orchestrate() {
        pincerTaskConfig.orchestrate()
    }

    fun get(): Provider<CreationData> {
        // A hacky approach, to bring latestTaskProvider as dependency of which uses
        // the provider later.
        return latestTaskProvider.map {
            pincerTaskConfig.data.get()
        }
    }

    fun transform(
        taskProvider: TaskProvider<*>
    ): Provider<CreationData> {
        taskProvider.configure { dependsOn(latestTaskProvider) }
        latestTaskProvider = taskProvider
        return pincerTaskConfig.data
    }

}

class SingleArtifactContainer<FileTypeT : FileSystemLocation>(
    artifactType: PolyfilledArtifact<*>,
    project: Project,
    variant: Variant
) : ArtifactContainer<FileTypeT>(
    artifactType,
    project,
    variant,
    project.extensions.getByType(PolyfillExtension::class.java).singleArtifactMap
)

class MultipleArtifactContainer<FileTypeT : FileSystemLocation>(
    artifactType: PolyfilledArtifact<*>,
    project: Project,
    variant: Variant,
) : ArtifactContainer<List<FileTypeT>>(
    artifactType,
    project,
    variant,
    project.extensions.getByType(PolyfillExtension::class.java).multipleArtifactMap
)