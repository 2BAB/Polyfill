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

abstract class ArtifactContainer<CreationDataT, AdapterT : PropertyAdapter<CreationDataT>>(
    private val artifactType: PolyfilledArtifact<*>,
    private val project: Project,
    private val variant: Variant,
    private val allocator: () -> AdapterT,
    private val map: Map<PolyfilledArtifact<*>, KClass<out PincerTaskConfiguration<*>>>
) {

    private val headTaskProvider: TaskProvider<*> = project.tasks.register(
        "virtualHeadOf${artifactType::class.simpleName}For${variant.getCapitalizedName()}"
    )
    private var latestTaskProvider: TaskProvider<*> = headTaskProvider
    private val pincerTaskConfig: PincerTaskConfiguration<CreationDataT>
    private var dataProvider = allocator()

    init {
        val configureAction = map[artifactType]!! as (KClass<out PincerTaskConfiguration<CreationDataT>>)
        pincerTaskConfig = configureAction.constructors.first().call(
            project, variant, headTaskProvider, { latestTaskProvider }
        )
        pincerTaskConfig.orchestrate()
        dataProvider.set(headTaskProvider.map {
            pincerTaskConfig.data.get()
        })
    }

    internal fun orchestrate() {
        pincerTaskConfig.orchestrate()
    }

    fun get(): Provider<CreationDataT> {
        return dataProvider.get()
    }

    fun transform(
        taskProvider: TaskProvider<*>
    ): Provider<CreationDataT> {
        taskProvider.configure { dependsOn(latestTaskProvider) }
        latestTaskProvider = taskProvider
        dataProvider.set(
            // A hacky approach, to bring latestTaskProvider as dependency of which uses
            // the provider later.
            latestTaskProvider.map {
                pincerTaskConfig.data.get()
            }
        )
        return pincerTaskConfig.data
    }

}

class SingleArtifactContainer<FileTypeT : FileSystemLocation>(
    artifactType: PolyfilledArtifact<*>,
    project: Project,
    variant: Variant,
    allocator: () -> SinglePropertyAdapter<FileTypeT>
) : ArtifactContainer<FileTypeT, SinglePropertyAdapter<FileTypeT>>(
    artifactType,
    project,
    variant,
    allocator,
    project.extensions.getByType(PolyfillExtension::class.java).singleArtifactMap
)

class MultipleArtifactContainer<FileTypeT : FileSystemLocation>(
    artifactType: PolyfilledArtifact<*>,
    project: Project,
    variant: Variant,
    allocator: () -> MultiplePropertyAdapter<FileTypeT>
) : ArtifactContainer<List<FileTypeT>, MultiplePropertyAdapter<FileTypeT>>(
    artifactType,
    project,
    variant,
    allocator,
    project.extensions.getByType(PolyfillExtension::class.java).multipleArtifactMap
)
