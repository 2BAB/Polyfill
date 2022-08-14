package me.xx2bab.polyfill.artifact

import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.PolyfillAction
import me.xx2bab.polyfill.PolyfillExtension
import me.xx2bab.polyfill.PolyfilledArtifact
import me.xx2bab.polyfill.task.TaskExtendConfiguration
import org.gradle.api.Project
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import kotlin.reflect.KClass

/**
 * For per artifact delegation.
 */
abstract class ArtifactContainer<CreationDataT>(
    private val artifactType: PolyfilledArtifact<*>,
    private val project: Project,
    private val variant: Variant,
    private val map: Map<PolyfilledArtifact<*>, KClass<out TaskExtendConfiguration<*>>>
) {

    private val taskExtConfig: TaskExtendConfiguration<CreationDataT>
    private val actionList: MutableList<PolyfillAction<CreationDataT>> = mutableListOf()

    init {
        val configureAction = map[artifactType]!! as (KClass<out TaskExtendConfiguration<CreationDataT>>)
        taskExtConfig = configureAction.constructors.first().call(
            project, variant, { actionList }
        )
        taskExtConfig.orchestrate()
    }

    fun get(): Provider<CreationDataT> {
        return taskExtConfig.data
    }

    fun inPlaceUpdate(action: PolyfillAction<CreationDataT>) {
        actionList.add(action)
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
    variant: Variant
) : ArtifactContainer<List<FileTypeT>>(
    artifactType,
    project,
    variant,
    project.extensions.getByType(PolyfillExtension::class.java).multipleArtifactMap
)
