package me.xx2bab.polyfill

import org.gradle.api.Task
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

interface ArtifactsStorage<PluginTypeT : PolyfilledPluginType> {

    fun <FileTypeT : FileSystemLocation> get(
        type: PolyfilledSingleArtifact<FileTypeT, PluginTypeT>
    ): Provider<FileTypeT>

    fun <FileTypeT : FileSystemLocation> getAll(
        type: PolyfilledMultipleArtifact<FileTypeT, PluginTypeT>
    ): Provider<List<FileTypeT>>

    fun <TaskT : Task, FileTypeT : FileSystemLocation> use(
        taskProvider: TaskProvider<TaskT>,
        wiredWith: (TaskT) -> Property<FileTypeT>,
        toInPlaceUpdate: PolyfilledSingleArtifact<FileTypeT, PluginTypeT>
    )

    fun <TaskT : Task, FileTypeT : FileSystemLocation> use(
        taskProvider: TaskProvider<TaskT>,
        wiredWith: (TaskT) -> ListProperty<FileTypeT>,
        toInPlaceUpdate: PolyfilledMultipleArtifact<FileTypeT, PluginTypeT>
    )

}

