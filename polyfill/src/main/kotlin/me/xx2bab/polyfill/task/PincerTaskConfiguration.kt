package me.xx2bab.polyfill.task

import com.android.build.api.variant.Variant
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

abstract class PincerTaskConfiguration<CreationDataT, PropertyT, TaskT : Task>(
    val project: Project,
    val variant: Variant,
    var lazyLastTaskProvider: () -> TaskProvider<*>
) {

    abstract val initialTaskProvider: TaskProvider<TaskT>

    abstract val tailTaskProviders: List<TaskProvider<*>>

    abstract val data: Provider<CreationDataT>

    abstract val from: (TaskT) -> PropertyT

    abstract fun orchestrate(): Pair<TaskProvider<*>, Provider<CreationDataT>>

}

abstract class SingleArtifactPincerTaskConfiguration<FileTypeT: FileSystemLocation, TaskT : Task>(
    project: Project,
    variant: Variant,
    lazyLastTaskProvider: () -> TaskProvider<*>
) : PincerTaskConfiguration<FileTypeT, Property<FileTypeT>, TaskT>(project, variant, lazyLastTaskProvider) {
    override fun orchestrate(): Pair<TaskProvider<*>, Provider<FileTypeT>> {
        initialTaskProvider.configure {
            from(this).set(data)
        }
        return Pair(initialTaskProvider, initialTaskProvider.flatMap { from(it) })
    }
}


abstract class MultipleArtifactPincerTaskConfiguration<FileTypeT: FileSystemLocation, TaskT : Task>(
    project: Project,
    variant: Variant,
    lazyLastTaskProvider: () -> TaskProvider<*>
) : PincerTaskConfiguration<List<FileTypeT>, ListProperty<FileTypeT>, TaskT>(project, variant, lazyLastTaskProvider) {
    override fun orchestrate(): Pair<TaskProvider<*>, Provider<List<FileTypeT>>> {
        initialTaskProvider.configure {
            from(this).set(data)
        }
        return Pair(initialTaskProvider, initialTaskProvider.flatMap { from(it) })
    }
}