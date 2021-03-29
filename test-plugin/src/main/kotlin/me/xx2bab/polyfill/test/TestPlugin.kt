package me.xx2bab.polyfill.test

import com.alibaba.fastjson.JSON
import me.xx2bab.polyfill.Polyfill
import me.xx2bab.polyfill.manifest.source.ManifestAfterMergeListener
import me.xx2bab.polyfill.manifest.source.ManifestBeforeMergeListener
import me.xx2bab.polyfill.manifest.source.ManifestMergeInputProvider
import me.xx2bab.polyfill.manifest.source.ManifestMergeOutputProvider
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

class TestPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.afterEvaluate {
            mkdir(project.rootProject.buildDir.absolutePath
                    + File.separator + "functionTestOutput")
        }

        // 0. Gets Polyfill instance with Project instance
        val polyfill = Polyfill.createApplicationPolyfill(project)

        // 1. Starts onVariantProperties
        polyfill.onVariantProperties {
            val variant = this
            // 3. Create & Config the hook task.
            val preUpdateTask = project.tasks.register("preUpdate${variant.name.capitalize()}Manifest",
                    ManifestBeforeMergeTask::class.java) {
                beforeMergeInputs.set(polyfill.getProvider(variant, ManifestMergeInputProvider::class.java).get())
            }
            // 4. Add it with the listener (which plays the role of entry for a hook).
            val beforeMergeListener = ManifestBeforeMergeListener(preUpdateTask)
            polyfill.addAGPTaskListener(variant, beforeMergeListener)


            // Let's try again with after merge hook
            val postUpdateTask = project.tasks.register("postUpdate${variant.name.capitalize()}Manifest",
                    ManifestAfterMergeTask::class.java) {
                afterMergeInputs.set(polyfill.getProvider(variant, ManifestMergeOutputProvider::class.java).get())
            }
            val afterMergeListener = ManifestAfterMergeListener(postUpdateTask)
            polyfill.addAGPTaskListener(variant, afterMergeListener)
        }
    }


    // 2. Prepare the task containing specific hook logic.
    abstract class ManifestBeforeMergeTask : DefaultTask() {
        @get:InputFiles
        abstract val beforeMergeInputs: SetProperty<FileSystemLocation>

        @TaskAction
        fun beforeMerge() {
            val manifestPathsOutput = getOutputFile(project, "manifest-merge-input.json")
            manifestPathsOutput.createNewFile()
            beforeMergeInputs.get().let { set ->
                manifestPathsOutput.writeText(JSON.toJSONString(set.map { it.asFile.absolutePath }))
            }
        }
    }

    abstract class ManifestAfterMergeTask : DefaultTask() {

        @get:InputFiles
        abstract val afterMergeInputs: RegularFileProperty

        @TaskAction
        fun afterMerge() {
            if (afterMergeInputs.isPresent) {
                val file = afterMergeInputs.get().asFile
                val modifiedManifest = file.readText()
                        .replace("allowBackup=\"true\"", "allowBackup=\"false\"")
                file.writeText(modifiedManifest)
            }
        }

    }

    companion object {
        fun getOutputFile(project: Project,
                          fileName: String): File {
            return File(project.rootProject.buildDir,
                    "functionTestOutput" + File.separator + fileName).apply {
                createNewFile()
            }
        }
    }

}