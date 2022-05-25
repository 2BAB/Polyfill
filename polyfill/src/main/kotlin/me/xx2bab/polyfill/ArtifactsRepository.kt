package me.xx2bab.polyfill

import com.android.build.api.artifact.Artifacts
import com.android.build.api.artifact.TaskBasedOperation
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider

/**
 * The polyfill version of [Artifacts], to access more intermediate artifacts
 * on a Variant object. To know more about Variant&Artifact APIs, please refer to
 * [Configure build variants](https://developer.android.com/studio/build/build-variants)
 * and [Extend Android Gradle Plugin](https://developer.android.com/studio/build/extend-agp).
 */
interface ArtifactsRepository<PluginTypeT : PolyfilledPluginType> {

    /**
     * The polyfill version of [Artifacts.get]. For the usage can refer to [getall] comments.
     *
     * @param type The target artifact type, must be the internal object of [PolyfilledSingleArtifact].
     * @return The artifact wrapper by [Provider] that can be consumed by TaskProvider configuration.
     */
    fun <FileTypeT : FileSystemLocation> get(
        type: PolyfilledSingleArtifact<FileTypeT, PluginTypeT>
    ): Provider<FileTypeT>

    /**
     * The polyfill version of [Artifacts.getAll].
     *
     * ``` Kotlin
     * val androidExtension = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
     * androidExtension.onVariants { variant ->
     *     val printManifestTask = project.tasks.register<PreUpdateManifestsTask>(
     *         "getAllInputManifestsFor${variant.name.capitalize()}") {
     *             beforeMergeInputs.set(
     *                 variant.artifactsPolyfill.getAll(PolyfilledMultipleArtifact.ALL_MANIFESTS)
     *             )
     *     }
     * }
     * ```
     * @param type The target artifact type, must be the internal object of [PolyfilledMultipleArtifact].
     * @return The artifact wrapper by [Provider] that can be consumed by TaskProvider configuration.
     */
    fun <FileTypeT : FileSystemLocation> getAll(
        type: PolyfilledMultipleArtifact<FileTypeT, PluginTypeT>
    ): Provider<List<FileTypeT>>

    /**
     * The polyfill version of [Artifacts.use], it also combines the [TaskBasedOperation] flow.
     * It's not feasible for external plugins to provide `toTransform()` `toCreate()` `toAppend()`
     * since 3rd party developers can not modify the internal data flow of AGP tasks. Instead of the
     * original pipeline, we could build a simple data flow which modifies files in place to make it
     * easier for plugin authors to work on - that is about `toInPlaceUpdate`. We continue leverage
     * the classic `dependsOn()` to orchestrate our flow and make sure [get] [getAll] related tasks are
     * scheduled after `toInPlaceUpdate` tasks.
     *
     * ``` Kotlin
     * val androidExtension = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
     * androidExtension.onVariants { variant ->
     *     val preHookManifestTask = project.tasks.register<PreUpdateManifestsTask>(
     *         "preUpdate${variant.name.capitalize()}Manifest")
     *     variant.artifactsPolyfill.use(
     *         taskProvider = preHookManifestTask2,
     *         wiredWith = PreUpdateManifestsTask::beforeMergeInputs,
     *         toInPlaceUpdate = PolyfilledMultipleArtifact.ALL_MANIFESTS
     *     )
     * }
     *
     * ...
     *
     * abstract class PreUpdateManifestsTask : DefaultTask() {
     *     @get:InputFiles
     *     abstract val beforeMergeInputs: ListProperty<RegularFile>

     *     @TaskAction
     *     fun beforeMerge() {
     *         beforeMergeInputs.get().forEach {
     *             val path = it.asFile.absolutePath
     *             ...
     *         }
     *     }
     * }
     * ```
     *
     * @param action The Action which will be added to a target task to modify/update target artifact.
     * @param toInPlaceUpdate The target artifact type, must be the internal object of [PolyfilledSingleArtifact].
     */
    fun <FileTypeT : FileSystemLocation> use(
        action: DependentAction<FileTypeT>,
        toInPlaceUpdate: PolyfilledSingleArtifact<FileTypeT, PluginTypeT>
    )

    /**
     * The polyfill version of [Artifacts.use], same as [use] above.
     *
     * @param action The Action which will be added to a target task to modify/update target artifacts.
     * @param toInPlaceUpdate The target artifact type, must be the internal object of [PolyfilledMultipleArtifact].
     */
    fun <FileTypeT : FileSystemLocation> use(
        action: DependentAction<List<FileTypeT>>,
        toInPlaceUpdate: PolyfilledMultipleArtifact<FileTypeT, PluginTypeT>
    )

}

