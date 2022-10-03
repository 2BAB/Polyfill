package me.xx2bab.polyfill.test

import com.alibaba.fastjson.JSON
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.android.sdklib.BuildToolInfo
import me.xx2bab.polyfill.*
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
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
        val buildDir = project.rootProject.buildDir
        val androidExtension = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        androidExtension.onVariants { variant ->
            // BuildToolInfo
            val buildToolInfo = variant.getBuildToolInfo(project).get()
            val aapt2PathGenTask = project.tasks.register<WorkingWithAapt2Task>(
                "writeAapt2PathFor${variant.name.capitalize()}"
            ) {
                aapt2 = buildToolInfo.getPath(BuildToolInfo.PathId.AAPT2)
                this.buildDir = buildDir
            }
            project.afterEvaluate {
                variant.getTaskContainer().assembleTask.dependsOn(aapt2PathGenTask)
            }

            // ALL_MANIFESTS
            val printManifestTask = project.tasks.register<PrintAllManifestsTask>(
                "getAllInputManifestsFor${variant.name.capitalize()}"
            ) {
                beforeMergeInputs.set(
                    variant.artifactsPolyfill
                        .getAll(PolyfilledMultipleArtifact.ALL_MANIFESTS)
                )
                this.buildDir = buildDir
            }
            project.afterEvaluate {
                variant.getTaskContainer().assembleTask.dependsOn(printManifestTask)
            }

            val preHookManifestTaskAction1 = PreUpdateManifestsTaskAction(buildDir, id = "preHookManifestTaskAction1")
            variant.artifactsPolyfill.use(
                action = preHookManifestTaskAction1,
                toInPlaceUpdate = PolyfilledMultipleArtifact.ALL_MANIFESTS
            )

            val preHookManifestTaskAction2 = PreUpdateManifestsTaskAction(buildDir, id = "preHookManifestTaskAction2")
            variant.artifactsPolyfill.use(
                action = preHookManifestTaskAction2,
                toInPlaceUpdate = PolyfilledMultipleArtifact.ALL_MANIFESTS
            )

            // ALL_RESOURCES & MERGED_RESOURCES
//            val allInputResources = variant.artifactsPolyfill
//                .getAll(PolyfilledMultipleArtifact.ALL_RESOURCES)
//            val postUpdateResourceTaskAction = PostUpdateResourceTask(buildDir, allInputResources)
//            variant.artifactsPolyfill.use(
//                action = postUpdateResourceTaskAction,
//                toInPlaceUpdate = PolyfilledSingleArtifact.MERGED_RESOURCES
//            )

            // ALL_JAVA_RES
            val printAllJavaResTask = project.tasks.register<PrintAllJavaResourcesTask>(
                "getAllInputJavaResFor${variant.name.capitalize()}"
            ) {
                beforeMergeInputs.set(
                    variant.artifactsPolyfill
                        .getAll(PolyfilledMultipleArtifact.ALL_JAVA_RES)
                )
                this.buildDir = buildDir
            }
            project.afterEvaluate {
                variant.getTaskContainer().assembleTask.dependsOn(printAllJavaResTask)
            }

            val preUpdateJavaResTaskAction = PreUpdateJavaResourcesTask(buildDir, "preUpdateJavaResTaskAction")
            variant.artifactsPolyfill.use(
                action = preUpdateJavaResTaskAction,
                toInPlaceUpdate = PolyfilledMultipleArtifact.ALL_JAVA_RES
            )
        }

//        project.gradle.taskGraph.whenReady {
//            val deps = getDependencies(project.tasks.getByName("getAllInputManifestsForDebug"))
//            val taskDepsTxt = getOutputFile(buildDir, "get-all-input-manifests-for-debug-task-deps.txt")
//            taskDepsTxt.createNewFile()
//            taskDepsTxt.writeText(deps.joinToString(", ") { it.name })
//        }

        // project.extensions.getByType<PolyfillExtension>()
        //    .registerTaskExtensionConfig(DUMMY_SINGLE_ARTIFACT, DummySingleArtifactImpl::class)
    }


    abstract class WorkingWithAapt2Task: DefaultTask() {
        @get:Input
        var aapt2: String = ""

        @get:Internal
        var buildDir: File? = null

        @TaskAction
        fun beforeMerge() {
            val aapt2PathOutput = getOutputFile(buildDir!!, "aapt2-path.txt")
            aapt2PathOutput.createNewFile()
            aapt2PathOutput.writeText(aapt2)
        }
    }


    abstract class PrintAllManifestsTask: DefaultTask() {
        @get:InputFiles
        abstract val beforeMergeInputs: ListProperty<RegularFile>

        @get:Internal
        var buildDir: File? = null

        @TaskAction
        fun beforeMerge() {
            val manifestPathsOutput = getOutputFile(buildDir!!, "all-manifests-by-${name}.json")
            manifestPathsOutput.createNewFile()
            beforeMergeInputs.get().let { files ->
                manifestPathsOutput.writeText(JSON.toJSONString(files.map { it.asFile.absolutePath }))
            }
        }
    }


    class PreUpdateManifestsTaskAction(
        private val buildDir: File,
        private val id: String
    ) : PolyfillAction<List<RegularFile>> {
        override fun onTaskConfigure(task: Task) {
        }

        override fun onExecute(beforeMergeInputs: Provider<List<RegularFile>>) {
            val manifestPathsOutput = getOutputFile(buildDir, "all-manifests-by-${id}.json")
            manifestPathsOutput.createNewFile()
            beforeMergeInputs.get().let { files ->
                manifestPathsOutput.writeText(JSON.toJSONString(files.map { it.asFile.absolutePath }))
            }
        }
    }

    class PostUpdateResourceTask(
        private val buildDir: File,
        private val beforeMergeInputs: Provider<List<Directory>>
    ) : PolyfillAction<Directory> {
        override fun onTaskConfigure(task: Task) {
        }

        override fun onExecute(compiledFilesDir: Provider<Directory>) {
            val allResourcesInputJSONFile = getOutputFile(buildDir, "all-resources.json")
            allResourcesInputJSONFile.createNewFile()
            beforeMergeInputs.get().let { set ->
                allResourcesInputJSONFile.writeText(JSON.toJSONString(set.map { it.asFile.absolutePath }))
            }
            val resourcePathsOutput = getOutputFile(buildDir, "merged-resource-dir.txt")
            resourcePathsOutput.createNewFile()
            resourcePathsOutput.writeText(compiledFilesDir.get().asFile.absolutePath)
        }
    }


    abstract class PrintAllJavaResourcesTask: DefaultTask() {
        @get:InputFiles
        abstract val beforeMergeInputs: ListProperty<RegularFile>

        @get:Internal
        var buildDir: File? = null

        @TaskAction
        fun beforeMerge() {
            val manifestPathsOutput = getOutputFile(buildDir!!, "all-java-res-by-${name}.json")
            manifestPathsOutput.createNewFile()
            beforeMergeInputs.get().let { files ->
                manifestPathsOutput.writeText(JSON.toJSONString(files.map { it.asFile.absolutePath }))
            }
        }
    }

    class PreUpdateJavaResourcesTask(
        private val buildDir: File,
        private val id: String
    ) : PolyfillAction<List<RegularFile>> {
        override fun onTaskConfigure(task: Task) {
        }

        override fun onExecute(beforeMergeInputs: Provider<List<RegularFile>>) {
            val javaResPathsOutput = getOutputFile(buildDir, "all-java-res-by-${id}.json")
            javaResPathsOutput.createNewFile()
            beforeMergeInputs.get().let { files ->
                javaResPathsOutput.writeText(JSON.toJSONString(files.map { it.asFile.absolutePath }))
            }
        }
    }


    companion object {
        fun getOutputFile(
            buildDir: File,
            fileName: String
        ): File {
            return File(
                buildDir,
                "functionTestOutput" + File.separator + fileName
            ).apply {
                createNewFile()
            }
        }
    }

}
