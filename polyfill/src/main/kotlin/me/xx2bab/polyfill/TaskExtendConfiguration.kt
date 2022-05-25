package me.xx2bab.polyfill.task

import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.ArtifactsRepository
import me.xx2bab.polyfill.DependentAction
import me.xx2bab.polyfill.PolyfilledMultipleArtifact
import me.xx2bab.polyfill.PolyfilledSingleArtifact
import me.xx2bab.polyfill.artifact.ArtifactContainer
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
 * Nevertheless, as an external library, it is not able to modify the AGP and its Artifacts' work flow.
 * Ways on how we retrieve data (Providers) are various and hacky, therefore it's infeasible for Polyfill
 * to bind them to existing AGP tasks, we still have to deal with [data] retrieve and [orchestrate] process
 * respectively.
 *
 * To archive our hook, Polyfill introduces its own pipeline mechanism. We consider two general scenarios for
 * Task pipelines:
 *
 * 1. The focus is "Task Input" of *SuccessorTaskOfAGP, the hook point should be placed before it,
 *   our *Input Data* may come from:
 *   1.1 A copy of SuccessorTaskOfAGP task input
 *   1.2 A potential PredecessorTaskOfAGP
 *   1.3 Other internal AGP components that you can access from [VariantExtension], such as
 *        [VariantExtension#ApplicationVariant.getApkCreationConfigImpl().config.artifacts]
 *
 * (PredecessorTaskOfAGP) --> InPlaceUpdateTask1 --> ... --> InPlaceUpdateTaskN --> *SuccessorTaskOfAGP
 *                                                                                 └─> Get(All)Task1..N
 *
 * 2. The focus is "Task Output" of PredecessorsTaskOfAGP*, the hook pint should be placed after it,
 *   our *Input Data* should come from PredecessorsTaskOfAGP* "Task Output" directly, however to make
 *   InPlaceUpdateTask run automatically along with other built-in AGP tasks, we need to link InPlaceUpdateTaskN
 *   to a SuccessorTaskOfAGP(s) which is(are) the() consumer(s) of aforementioned "Task Output".
 *
 * PredecessorTaskOfAGP* --> InPlaceUpdateTask1 --> ... --> InPlaceUpdateTaskN --> SuccessorTaskOfAGP(s)
 *                                                                                └─> Get(All)Task1..N
 *
 * Get(All) tasks here should run after all InPlaceUpdateTasks completed, to get final results of *Input Data*.
 * From our end we do not care about if they will run independently or are associated with some other AGP tasks,
 * above graphs only denote their predecessors and that's about it. (Please do not take them as the
 * parallel-execution since the actual sequence is not predicable from current stage.)
 *
 * We have made it clear for scenarios and how to retrieve *Input Data* on per section, now it comes
 * to *Task Dependencies*. The pipeline of InPlaceUpdateTasks can be easily set up using linked-list
 * approach, check the impl of [ArtifactContainer.inPlaceUpdate]. However, if there is no transformation
 * tasks that were set from external, Get(All) tasks should inherit *Input Data* from data source directly.
 * To keep neat of the code base, we can leverage a common trick when solve linked-list problem - virtual head.
 * Thus, [dependentTask] is designed to be passed at all times, while [lazyTailTaskProvider] is a lazy
 * variable that consumed later when the chain is ready.
 *
 * [orchestrate] is designed to schedule above two [TaskProvider]s. To be noticed, [orchestrate] is executed
 * immediately once the [TaskExtendConfiguration] instance is created, at this moment many other dependencies
 * are not ready to interact with, developers who implement this function should put the logic into a post
 * Gradle lifecycle callback, such as [Project.afterEvaluate] / [TaskCollection.whenTaskAdded].
 *
 * In a nutshell, don't forget to set up both left/right flank of tasks -> a pincer-like configuration.
 *
 * @param dependentTask Head [TaskProvider] of InPlaceUpdateTask chain.
 * @param lazyTailTaskProvider Tail [TaskProvider] of InPlaceUpdateTask chain.
 */
abstract class TaskExtendConfiguration<CreationDataT>(
    val project: Project,
    val variant: Variant,
    var actionList: () -> List<DependentAction<CreationDataT>>
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
abstract class SingleArtifactPincerTaskConfiguration<FileTypeT : FileSystemLocation>(
    project: Project,
    variant: Variant,
    actionList: () -> List<DependentAction<FileTypeT>>
) : TaskExtendConfiguration<FileTypeT>(project, variant, actionList)

/**
 * A dedicated [TaskExtendConfiguration] for configuring [PolyfilledMultipleArtifact].
 * It provides data in `Provider<List<[FileTypeT]>>` type.
 */
abstract class MultipleArtifactPincerTaskConfiguration<FileTypeT : FileSystemLocation>(
    project: Project,
    variant: Variant,
    actionList: () -> List<DependentAction<List<FileTypeT>>>
) : TaskExtendConfiguration<List<FileTypeT>>(project, variant, actionList)