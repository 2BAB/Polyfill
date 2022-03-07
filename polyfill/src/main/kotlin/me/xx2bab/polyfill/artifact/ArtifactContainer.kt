package me.xx2bab.polyfill.artifact

import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.PolyfillExtension
import me.xx2bab.polyfill.PolyfilledArtifact
import me.xx2bab.polyfill.task.PincerTaskConfiguration
import org.gradle.api.Project
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

abstract class ArtifactContainer<T, U>(
    private val artifactType: PolyfilledArtifact<*>,
    private val project: Project,
    private val variant: Variant,
    private val allocator: () -> U,
    private val map: Map<PolyfilledArtifact<*>, KClass<out PincerTaskConfiguration<*, *, *>>>
) where U: PropertyAdapter<T>{

    private lateinit var latestTaskProvider: TaskProvider<*>
    private var dataProvider = allocator()

    init {
        val configureAction = map[artifactType]!!
        val pincerTaskConfig = configureAction.constructors.first().call(
            project, variant, { latestTaskProvider }
        )
        val initData = pincerTaskConfig.orchestrate()
        latestTaskProvider = initData.first
        dataProvider.set(initData.second as Provider<T>)
    }

    fun get(): Provider<T> = dataProvider.get()

    fun transform(
        taskProvider: TaskProvider<*>
    ): Provider<T> {
        taskProvider.configure {
            dependsOn(latestTaskProvider)
        }
        latestTaskProvider = taskProvider
        return dataProvider.get()
    }

}

class SingleArtifactContainer<FileTypeT: FileSystemLocation>(
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
