package me.xx2bab.polyfill.test

import com.alibaba.fastjson.JSON
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.internal.tasks.factory.dependsOn
import me.xx2bab.polyfill.PolyfilledMultipleArtifact
import me.xx2bab.polyfill.PolyfilledSingleArtifact
import me.xx2bab.polyfill.artifactsPolyfill
import me.xx2bab.polyfill.getTaskContainer
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.register
import java.io.File

class TestPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.apply(plugin = "me.2bab.polyfill")
        project.afterEvaluate {
            mkdir(
                project.rootProject.buildDir.absolutePath
                        + File.separator + "functionTestOutput"
            )
        }
        val androidExtension = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        androidExtension.onVariants { variant ->
            val printManifestTask = project.tasks.register<PreUpdateManifestsTask>(
                "getAllInputManifestsFor${variant.name.capitalize()}"
            ) {
                beforeMergeInputs.set(
                    variant.artifactsPolyfill
                        .getAll(PolyfilledMultipleArtifact.ALL_MANIFESTS)
                )
            }
            project.afterEvaluate {
                variant.getTaskContainer().assembleTask.dependsOn(printManifestTask)
            }

            val preHookManifestTask1 = project.tasks.register<PreUpdateManifestsTask>(
                "preUpdate${variant.name.capitalize()}Manifest1"
            )
            variant.artifactsPolyfill.use(
                taskProvider = preHookManifestTask1,
                wiredWith = PreUpdateManifestsTask::beforeMergeInputs,
                toInPlaceUpdate = PolyfilledMultipleArtifact.ALL_MANIFESTS
            )

            val preHookManifestTask2 = project.tasks.register<PreUpdateManifestsTask>(
                "preUpdate${variant.name.capitalize()}Manifest2"
            )
            variant.artifactsPolyfill.use(
                taskProvider = preHookManifestTask2,
                wiredWith = PreUpdateManifestsTask::beforeMergeInputs,
                toInPlaceUpdate = PolyfilledMultipleArtifact.ALL_MANIFESTS
            )


            val postUpdateResourceTask = project.tasks.register<PostUpdateResourceTask>(
                "postUpdate${variant.name.capitalize()}Resources"
            ) {
                beforeMergeInputs.set(
                    variant.artifactsPolyfill
                        .getAll(PolyfilledMultipleArtifact.ALL_RESOURCES)
                )
            }
            variant.artifactsPolyfill.use(
                taskProvider = postUpdateResourceTask,
                wiredWith = PostUpdateResourceTask::compiledFilesDir,
                toInPlaceUpdate = PolyfilledSingleArtifact.MERGED_RESOURCES
            )
        }

        project.gradle.taskGraph.whenReady {
            val deps = getDependencies(project.tasks.getByName("getAllInputManifestsForDebug"))
            val taskDepsTxt = getOutputFile(project, "get-all-input-manifests-for-debug-task-deps.txt")
            taskDepsTxt.createNewFile()
            taskDepsTxt.writeText(deps.joinToString(", ") { it.name })
        }

        // project.extensions.getByType<PolyfillExtension>()
        //    .registerPincerTaskConfig(DUMMY_SINGLE_ARTIFACT, DummySingleArtifactImpl::class)
    }

    abstract class PreUpdateManifestsTask : DefaultTask() {
        @get:InputFiles
        abstract val beforeMergeInputs: ListProperty<RegularFile>

        @TaskAction
        fun beforeMerge() {
            val manifestPathsOutput = getOutputFile(project, "all-manifests-by-${name}.json")
            manifestPathsOutput.createNewFile()
            beforeMergeInputs.get().let { files ->
                manifestPathsOutput.writeText(JSON.toJSONString(files.map { it.asFile.absolutePath }))
            }
        }
    }

    abstract class PostUpdateResourceTask : DefaultTask() {
        @get:InputFiles
        abstract val beforeMergeInputs: ListProperty<Directory>

        @get:InputDirectory
        @get:Optional
        abstract val compiledFilesDir: DirectoryProperty

        @TaskAction
        fun beforeMerge() {
            val allResourcesInputJSONFile = getOutputFile(project, "all-resources.json")
            allResourcesInputJSONFile.createNewFile()
            beforeMergeInputs.get().let { set ->
                allResourcesInputJSONFile.writeText(JSON.toJSONString(set.map { it.asFile.absolutePath }))
            }
            val resourcePathsOutput = getOutputFile(project, "merged-resource-dir.txt")
            resourcePathsOutput.createNewFile()
            resourcePathsOutput.writeText(compiledFilesDir.get().asFile.absolutePath)
        }
    }

    companion object {
        fun getOutputFile(
            project: Project,
            fileName: String
        ): File {
            return File(
                project.rootProject.buildDir,
                "functionTestOutput" + File.separator + fileName
            ).apply {
                createNewFile()
            }
        }
    }

}

