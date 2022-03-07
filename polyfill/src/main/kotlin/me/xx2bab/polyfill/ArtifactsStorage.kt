package me.xx2bab.polyfill

import com.android.build.api.variant.Variant
import org.gradle.api.Task
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

interface ArtifactsStorage<PluginTypeT: PolyfilledPluginType> {

    fun prepare(variant: Variant)

    fun <FileTypeT : FileSystemLocation> get(
        type: PolyfilledSingleArtifact<FileTypeT, PluginTypeT>
    ): Provider<FileTypeT>

    fun <FileTypeT : FileSystemLocation> getAll(
        type: PolyfilledMultipleArtifact<FileTypeT, PluginTypeT>
    ): Provider<List<FileTypeT>>

    fun <TaskT : Task, FileTypeT : FileSystemLocation> use(
        taskProvider: TaskProvider<TaskT>,
        wiredWith: (TaskT) -> Property<FileTypeT>,
        toTransformInPlace: PolyfilledSingleArtifact<FileTypeT, PluginTypeT>
    )

    fun <TaskT : Task, FileTypeT : FileSystemLocation> use(
        taskProvider: TaskProvider<TaskT>,
        wiredWith: (TaskT) -> ListProperty<FileTypeT>,
        toTransformInPlace: PolyfilledMultipleArtifact<FileTypeT, PluginTypeT>
    )

}

