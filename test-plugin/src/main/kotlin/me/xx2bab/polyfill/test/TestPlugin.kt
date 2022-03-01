package me.xx2bab.polyfill.test

import com.alibaba.fastjson.JSON
import com.android.build.api.variant.AndroidComponentsExtension
import me.xx2bab.polyfill.ApplicationVariantPolyfill
import me.xx2bab.polyfill.manifest.ManifestAfterMergeAction
import me.xx2bab.polyfill.manifest.ManifestBeforeMergeAction
import me.xx2bab.polyfill.manifest.ManifestMergeInputProvider
import me.xx2bab.polyfill.manifest.ManifestMergeOutputProvider
import me.xx2bab.polyfill.res.ResourcesBeforeMergeAction
import me.xx2bab.polyfill.res.ResourcesMergeInputProvider
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
            mkdir(
                project.rootProject.buildDir.absolutePath
                        + File.separator + "functionTestOutput"
            )
        }
        val androidExtension = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidExtension.onVariants { variant ->

            // 0. Get Polyfill instance with Project instance
            val polyfill = ApplicationVariantPolyfill(project, variant)

            // 1. Create & Config the hook task.
            val preUpdateTask = project.tasks.register(
                "preUpdate${variant.name.capitalize()}Manifest",
                ManifestBeforeMergeTask::class.java
            ) {
                val p = polyfill.newProvider(ManifestMergeInputProvider::class.java).obtain()
                beforeMergeInputs.set(p)
            }
            // 2. Add it with the action (which plays the role of entry for a hook).
            val beforeMergeAction = ManifestBeforeMergeAction(preUpdateTask)
            polyfill.addAGPTaskAction(beforeMergeAction)


            // Should use Variant API to replace it.
            // @see https://github.com/android/gradle-recipes/blob/agp-7.1/Kotlin/manifestTransformerTest/app/build.gradle.kts
            // @Deprecated ~~Let's try again with after merge hook~~
            val postUpdateTask = project.tasks.register(
                "postUpdate${variant.name.capitalize()}Manifest",
                ManifestAfterMergeTask::class.java
            ) {
                afterMergeInputs.set(polyfill.newProvider(ManifestMergeOutputProvider::class.java).obtain())
            }
            polyfill.addAGPTaskAction(ManifestAfterMergeAction(postUpdateTask))

            // To test ResourcesMergeInputProvider & ResourcesBeforeMergeAction
            val preUpdateResourceTask = project.tasks.register(
                "preUpdate${variant.name.capitalize()}Resources",
                ResourceBeforeMergeTask::class.java
            ) {
                val p = polyfill.newProvider(ResourcesMergeInputProvider::class.java).obtain()
                beforeMergeInputs.set(p)
            }
            polyfill.addAGPTaskAction(ResourcesBeforeMergeAction(preUpdateResourceTask))
        }

        // Optional: if the new Variant API can not fulfill the requirement
        // or you want to migrate from an old project to Polyfill smoothly,
        // you can use onClassicVariants{} instead.
        // Here are 2 samples which their used APIs are accessible for ApplicationVariant only,
        // though `polyfill.onVariants` is preferred as it uses new Variant API (Old APIs may get depracted).
        //
        // @see com.android.build.api.variant.ApplicationVariant
//        polyfill.onClassicVariants {
//            val applicationVariant = this
//            project.tasks
//                .register("makePolyfillCacheDirs${applicationVariant.name.capitalize()}") {
//                    // The versionName is only accessible by ApplicationVariant
//                    project.logger.info(applicationVariant.versionName)
//                }
//                .dependsOn(this.preBuildProvider) // The AGP task providers is only available by ApplicationVariant
//        }
    }


    // Prepare a task containing specific hook logic.
    abstract class ManifestBeforeMergeTask : DefaultTask() {
        @get:InputFiles
        abstract val beforeMergeInputs: SetProperty<FileSystemLocation>

        @TaskAction
        fun beforeMerge() {
            val manifestPathsOutput = getOutputFile(project, "manifests-merge-input.json")
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
