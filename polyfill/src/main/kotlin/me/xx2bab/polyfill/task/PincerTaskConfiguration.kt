package me.xx2bab.polyfill.task

import com.android.build.api.variant.Variant
import org.gradle.api.Project
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

abstract class PincerTaskConfiguration<CreationDataT>(
    val project: Project,
    val variant: Variant,
    val headTaskProvider: TaskProvider<*>,
    var lazyLastTaskProvider: () -> TaskProvider<*>?
) {
    abstract val data: Provider<CreationDataT>

    abstract fun orchestrate()
}

abstract class SingleArtifactPincerTaskConfiguration<FileTypeT: FileSystemLocation>(
    project: Project,
    variant: Variant,
    dummyHeadTaskProvider: TaskProvider<*>,
    lazyLastTaskProvider: () -> TaskProvider<*>?
) : PincerTaskConfiguration<FileTypeT>(project, variant, dummyHeadTaskProvider, lazyLastTaskProvider)


abstract class MultipleArtifactPincerTaskConfiguration<FileTypeT: FileSystemLocation>(
    project: Project,
    variant: Variant,
    dummyHeadTaskProvider: TaskProvider<*>,
    lazyLastTaskProvider: () -> TaskProvider<*>?
) : PincerTaskConfiguration<List<FileTypeT>>(project, variant, dummyHeadTaskProvider, lazyLastTaskProvider)