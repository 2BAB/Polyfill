package me.xx2bab.polyfill.test

import com.alibaba.fastjson.JSON
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.internal.tasks.factory.dependsOn
import me.xx2bab.polyfill.PolyfilledMultipleArtifact
import me.xx2bab.polyfill.PolyfilledSingleArtifact
import me.xx2bab.polyfill.agp.toTaskContainer
import me.xx2bab.polyfill.artifactsPolyfill
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
            val printManifestTask = project.tasks.register<ManifestBeforeMergeTask>(
                "printAllInputManifestsFor${variant.name.capitalize()}"
            ) {
                beforeMergeInputs.set(
                    variant.artifactsPolyfill
                        .getAll(PolyfilledMultipleArtifact.ALL_MANIFESTS)
                )
            }
            project.afterEvaluate {
                variant.toTaskContainer().assembleTask.dependsOn(printManifestTask)
            }

            val preHookManifestTask2 = project.tasks.register<ManifestBeforeMergeTask>(
                "preUpdate${variant.name.capitalize()}Manifest2"
            )
            variant.artifactsPolyfill.use(
                taskProvider = preHookManifestTask2,
                wiredWith = ManifestBeforeMergeTask::beforeMergeInputs,
                toInPlaceUpdate = PolyfilledMultipleArtifact.ALL_MANIFESTS
            )

            val preHookManifestTask3 = project.tasks.register<ManifestBeforeMergeTask>(
                "preUpdate${variant.name.capitalize()}Manifest3"
            )
            variant.artifactsPolyfill.use(
                taskProvider = preHookManifestTask3,
                wiredWith = ManifestBeforeMergeTask::beforeMergeInputs,
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
    }


    // Prepare a task containing specific hook logic.
    abstract class ManifestBeforeMergeTask : DefaultTask() {
        @get:InputFiles
        abstract val beforeMergeInputs: ListProperty<RegularFile>

        @TaskAction
        fun beforeMerge() {
            val manifestPathsOutput = getOutputFile(project, "manifests-merge-input-by-${name}.json")
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
            val resourcePathsOutput = getOutputFile(project, "resources-merge-input.json")
            resourcePathsOutput.createNewFile()
            beforeMergeInputs.get().let { set ->
                resourcePathsOutput.writeText(JSON.toJSONString(set.map { it.asFile.absolutePath }))
            }
            println("PostUpdateResourceTask: update with out dir - " + compiledFilesDir.get().asFile.absolutePath)
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
