package me.xx2bab.polyfill.test

import com.alibaba.fastjson.JSON
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import me.xx2bab.polyfill.ManifestCollection
import me.xx2bab.polyfill.artifactsPolyfill
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.InputFiles
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
            val preHookManifestTask = project.tasks.register<ManifestBeforeMergeTask>(
                "preUpdate${variant.name.capitalize()}Manifest"
            )
            variant.artifactsPolyfill.use(
                taskProvider = preHookManifestTask,
                wiredWith = ManifestBeforeMergeTask::beforeMergeInputs,
                toTransformInPlace = ManifestCollection
            )

            val preHookManifestTask2 = project.tasks.register<ManifestBeforeMergeTask>(
                "preUpdate${variant.name.capitalize()}Manifest2"
            )
            variant.artifactsPolyfill.use(
                taskProvider = preHookManifestTask2,
                wiredWith = ManifestBeforeMergeTask::beforeMergeInputs,
                toTransformInPlace = ManifestCollection
            )

            // To test ResourcesMergeInputProvider & ResourcesBeforeMergeAction
//            val preUpdateResourceTask = project.tasks.register(
//                "preUpdate${variant.name.capitalize()}Resources",
//                ResourceBeforeMergeTask::class.java
//            ) {
//                val p = polyfill.newProvider(ResourcesMergeInputProvider::class.java).obtain()
//                beforeMergeInputs.set(p)
//            }
//            polyfill.addAGPTaskAction(ResourcesBeforeMergeAction(preUpdateResourceTask))
        }

    }


    // Prepare a task containing specific hook logic.
    abstract class ManifestBeforeMergeTask : DefaultTask() {
        @get:InputFiles
        abstract val beforeMergeInputs: ListProperty<RegularFile>

        @TaskAction
        fun beforeMerge() {
            val manifestPathsOutput = getOutputFile(project, "manifests-merge-input.json")
            manifestPathsOutput.createNewFile()
            beforeMergeInputs.get().let { files ->
                manifestPathsOutput.writeText(JSON.toJSONString(files.map { it.asFile.absolutePath }))
            }
        }
    }

    abstract class ResourceBeforeMergeTask : DefaultTask() {
        @get:InputFiles
        abstract val beforeMergeInputs: SetProperty<FileSystemLocation>

        @TaskAction
        fun beforeMerge() {
            val resourcePathsOutput = getOutputFile(project, "resources-merge-input.json")
            resourcePathsOutput.createNewFile()
            beforeMergeInputs.get().let { set ->
                resourcePathsOutput.writeText(JSON.toJSONString(set.map { it.asFile.absolutePath }))
            }
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
