package me.xx2bab.polyfill.artifact

import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.*
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

abstract class DefaultArtifactsStorage<PluginTypeT : PolyfilledPluginType>(private val project: Project) :
    ArtifactsStorage<PluginTypeT> {

    private lateinit var variant: Variant
    private val storage = mutableMapOf<PolyfilledArtifact<*>, ArtifactContainer<*, *>>()

    override fun prepare(variant: Variant) {
        this.variant = variant
    }

    override fun <FileTypeT : FileSystemLocation> get(
        type: PolyfilledSingleArtifact<FileTypeT, PluginTypeT>
    ): Provider<FileTypeT> = getSingleArtifactContainer(type).get()


    override fun <FileTypeT : FileSystemLocation> getAll(
        type: PolyfilledMultipleArtifact<FileTypeT, PluginTypeT>
    ): Provider<List<FileTypeT>> = getMultipleArtifactContainer(type).get()

    override fun <TaskT : Task, FileTypeT : FileSystemLocation> use(
        taskProvider: TaskProvider<TaskT>,
        wiredWith: (TaskT) -> Property<FileTypeT>,
        toTransformInPlace: PolyfilledSingleArtifact<FileTypeT, PluginTypeT>
    ) {
        val dataProvider = getSingleArtifactContainer(toTransformInPlace).transform(taskProvider)
        taskProvider.configure { wiredWith(this).set(dataProvider) }
    }

    override fun <TaskT : Task, FileTypeT : FileSystemLocation> use(
        taskProvider: TaskProvider<TaskT>,
        wiredWith: (TaskT) -> ListProperty<FileTypeT>,
        toTransformInPlace: PolyfilledMultipleArtifact<FileTypeT, PluginTypeT>
    ) {
        val dataProvider = getMultipleArtifactContainer(toTransformInPlace).transform(taskProvider)
        taskProvider.configure { wiredWith(this).set(dataProvider) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <FileTypeT : FileSystemLocation> getSingleArtifactContainer(
        artifactType: PolyfilledSingleArtifact<FileTypeT, *>
    ): SingleArtifactContainer<FileTypeT> = storage.getOrPut(artifactType) {
        SingleArtifactContainer(artifactType, project, variant) {
            SinglePropertyAdapter(
                project.objects.property(artifactType.kind.dataType().java)
            )
        }
    } as SingleArtifactContainer<FileTypeT>

    @Suppress("UNCHECKED_CAST")
    private fun <FileTypeT : FileSystemLocation> getMultipleArtifactContainer(
        artifactType: PolyfilledMultipleArtifact<FileTypeT, *>
    ): MultipleArtifactContainer<FileTypeT> = storage.getOrPut(artifactType) {
        MultipleArtifactContainer(artifactType, project, variant) {
            MultiplePropertyAdapter(
                project.objects.listProperty(artifactType.kind.dataType().java)
            )
        }
    } as MultipleArtifactContainer<FileTypeT>

}

class ApplicationArtifactsStorage(p: Project) : DefaultArtifactsStorage<PolyfilledApplicationArtifact>(p)
class LibraryArtifactsStorage(p: Project) : DefaultArtifactsStorage<PolyfilledLibraryArtifact>(p)