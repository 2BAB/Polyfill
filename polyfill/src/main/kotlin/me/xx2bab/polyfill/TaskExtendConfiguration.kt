package me.xx2bab.polyfill.task

import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.ArtifactsRepository
import me.xx2bab.polyfill.PolyfillAction
import me.xx2bab.polyfill.PolyfilledMultipleArtifact
import me.xx2bab.polyfill.PolyfilledSingleArtifact
import org.gradle.api.Project
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.TaskProvider

/**
 * The core configuration action for artifact-consuming tasks to support
 * [ArtifactsRepository.get] / [ArtifactsRepository.getAll] / [ArtifactsRepository.use].
 *
 * To make them work, there are two major materials we have to prepare:
 *   - Input Data.
 *   - Running sequence adjustment (Task Dependencies).
 *
 * The Artifacts API of AGP leverages implicit task dependencies feature of [Property],
 * which attaches *Task Dependencies* on *Input Data*, check more from below link.
 * [Implicit Task Dependencies](https://docs.gradle.org/current/userguide/lazy_configuration.html#working_with_task_dependencies_in_lazy_properties)
 *
 * Nevertheless, as an external library, it is not able to modify the AGP and its Artifacts' work flow with
 * additional tasks. Ways on how we retrieve data (Providers) are various and hacky, therefore Polyfill finds
 * a fine approach to interact with the artifact within Task by adding more TaskActions. To bind them
 * to existing AGP tasks, we still have to deal with [data] retrieve and [orchestrate] process respectively.
 *
 * Get(All) functions here should run after all InPlaceUpdateTaskAction completed, to get final results of *Input Data*.
 * From our end we do not care about if they will run independently or are associated with some other AGP tasks,
 * above graphs only denote their predecessors and that's about it. (Please do not take them as the
 * parallel-execution since the actual sequence is not predicable from current stage.)
 *
 * [orchestrate] is designed to schedule above two [TaskProvider]s. To be noticed, [orchestrate] is executed
 * immediately once the [TaskExtendConfiguration] instance is created, at this moment many other dependencies
 * are not ready to interact with, developers who implement this function should put the logic into a post
 * Gradle lifecycle callback, such as [Project.afterEvaluate] / [TaskCollection.whenTaskAdded].
 *
 * @param actionList List of [PolyfillAction] that passed from users to receive artifacts and hence can update it in-place.
 */
abstract class TaskExtendConfiguration<CreationDataT>(
    val project: Project,
    val variant: Variant,
    var actionList: () -> List<PolyfillAction<CreationDataT>>
) {
    /**
     * To retrieve data from AGP internal components and export it as an Artifact
     * to external callers.
     * Please make use of [Provider] lazy configuration APIs to avoid eager consumption.
     */
    abstract val data: Provider<CreationDataT>

    /**
     * To set up task/action dependencies or initialize data lazily.
     */
    abstract fun orchestrate()

}

/**
 *  A dedicated [TaskExtendConfiguration] for configuring [PolyfilledSingleArtifact].
 *  It provides data in `Provider<[FileTypeT]>` type.
 */
abstract class SingleArtifactTaskExtendConfiguration<FileTypeT : FileSystemLocation>(
    project: Project,
    variant: Variant,
    actionList: () -> List<PolyfillAction<FileTypeT>>
) : TaskExtendConfiguration<FileTypeT>(project, variant, actionList)

/**
 * A dedicated [TaskExtendConfiguration] for configuring [PolyfilledMultipleArtifact].
 * It provides data in `Provider<List<[FileTypeT]>>` type.
 */
abstract class MultipleArtifactTaskExtendConfiguration<FileTypeT : FileSystemLocation>(
    project: Project,
    variant: Variant,
    actionList: () -> List<PolyfillAction<List<FileTypeT>>>
) : TaskExtendConfiguration<List<FileTypeT>>(project, variant, actionList)