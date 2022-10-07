package me.xx2bab.polyfill.test

import com.alibaba.fastjson.JSON
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.internal.scope.InternalArtifactType
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.android.sdklib.BuildToolInfo
import me.xx2bab.polyfill.*
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
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
                aapt2Path.set(buildToolInfo.getPath(BuildToolInfo.PathId.AAPT2))
                record.set(getOutputFile(buildDir, "aapt2-path.txt"))
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
                record.set(getOutputFile(buildDir, "all-manifests-by-${name}.json"))
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
            val allInputResources = variant.artifactsPolyfill
                .getAll(PolyfilledMultipleArtifact.ALL_RESOURCES)
            val postUpdateResourceTaskAction = PostUpdateResourceTaskAction(buildDir, allInputResources)
            variant.artifactsPolyfill.use(
                action = postUpdateResourceTaskAction,
                toInPlaceUpdate = PolyfilledSingleArtifact.MERGED_RESOURCES
            )

            // ALL_JAVA_RES
            val printAllJavaResTask = project.tasks.register<PrintAllJavaResourcesTask>(
                "getAllInputJavaResFor${variant.name.capitalize()}"
            ) {
                beforeMergeInputs.set(
                    variant.artifactsPolyfill
                        .getAll(PolyfilledMultipleArtifact.ALL_JAVA_RES)
                )
                record.set(getOutputFile(buildDir, "all-java-res-by-${name}.json"))
            }
            project.afterEvaluate {
                variant.getTaskContainer().assembleTask.dependsOn(printAllJavaResTask)
            }

            val javaResPathsOutput = project.objects.fileProperty().fileValue(
                getOutputFile(
                    buildDir,
                    "all-java-res-by-preUpdateJavaResTaskAction.json"
                )
            )
            val preUpdateJavaResTaskAction = PreUpdateJavaResourcesTaskAction(javaResPathsOutput)
            variant.artifactsPolyfill.use(
                action = preUpdateJavaResTaskAction,
                toInPlaceUpdate = PolyfilledMultipleArtifact.ALL_JAVA_RES
            )

            val getManifestMergeReportTask = project.tasks.register<GetManifestMergeReportTask>(
                "getManifestMergeReportFor${variant.name.capitalize()}"
            ) {
                this.report.set(
                    variant.artifactsPolyfill
                        .get(InternalArtifactType.MANIFEST_MERGE_REPORT)
                )
                this.record.set(
                    project.objects.fileProperty().fileValue(
                        getOutputFile(
                            buildDir,
                            "manifest-merger-debug-report.txt"
                        )
                    )
                )
            }
            project.afterEvaluate {
                variant.getTaskContainer().assembleTask.dependsOn(getManifestMergeReportTask)
            }
        }

        project.gradle.taskGraph.whenReady {
            val deps = getDependencies(project.tasks.getByName("getAllInputManifestsForDebug"))
            val taskDepsTxt = getOutputFile(buildDir, "get-all-input-manifests-for-debug-task-deps.txt")
            taskDepsTxt.createNewFile()
            taskDepsTxt.writeText(deps.joinToString(", ") { it.name })
        }

    }


    abstract class WorkingWithAapt2Task : DefaultTask() {

        @get:Input // This can be @Internal sometimes
        abstract val aapt2Path: Property<String>

        @get:OutputFile
        abstract val record: RegularFileProperty

        @TaskAction
        fun beforeMerge() {
            record.get().asFile.writeText(aapt2Path.get())
        }

    }


    abstract class PrintAllManifestsTask : DefaultTask() {

        @get:InputFiles
        abstract val beforeMergeInputs: ListProperty<RegularFile>

        @get:OutputFile
        abstract val record: RegularFileProperty

        @TaskAction
        fun beforeMerge() {
            beforeMergeInputs.get().let { files ->
                record.get().asFile.writeText(JSON.toJSONString(files.map { it.asFile.absolutePath }))
            }
        }

    }


    class PreUpdateManifestsTaskAction(
        buildDir: File,
        id: String
    ) : PolyfillAction<List<RegularFile>> {

        private val record = getOutputFile(buildDir, "all-manifests-by-${id}.json")

        override fun onTaskConfigure(task: Task) {}

        override fun onExecute(artifact: Provider<List<RegularFile>>) {
            artifact.get().let { files ->
                files.forEach {
                    val manifestFile = it.asFile
                    // Check per manifest input and filter whatever you want, remove broken pieces, etc.
                    // val updatedContent = manifestFile.readText().replace("abc", "def")
                    // manifestFile.writeText(updatedContent)
                }
                // Please do not write out new files in PolyfillAction for production usage
                // since it's intent to update artifacts in place. For consuming & generating new files,
                // you should use `get(...)` / `getAll(...)` with Task.
                // Here we just want to export something for testing.
                record.writeText(JSON.toJSONString(files.map { it.asFile.absolutePath }))
            }
        }
    }


    class PostUpdateResourceTaskAction(
        buildDir: File,
        private val beforeMergeInputs: Provider<List<Directory>>
    ) : PolyfillAction<Directory> {

        private val allResourcesInputJSONFile = getOutputFile(buildDir, "all-resources.json")
        private val resourcePathsOutput = getOutputFile(buildDir, "merged-resource-dir.txt")

        override fun onTaskConfigure(task: Task) {}

        override fun onExecute(artifact: Provider<Directory>) {
            allResourcesInputJSONFile.createNewFile()
            beforeMergeInputs.get().let { set ->
                allResourcesInputJSONFile.writeText(JSON.toJSONString(set.map { it.asFile.absolutePath }))
            }

            resourcePathsOutput.createNewFile()
            resourcePathsOutput.writeText(artifact.get().asFile.absolutePath)
        }
    }


    abstract class PrintAllJavaResourcesTask : DefaultTask() {

        @get:InputFiles
        abstract val beforeMergeInputs: ListProperty<RegularFile>

        @get:OutputFile
        abstract val record: RegularFileProperty

        @TaskAction
        fun beforeMerge() {
            beforeMergeInputs.get().let { files ->
                record.get().asFile.writeText(JSON.toJSONString(files.map { it.asFile.absolutePath }))
            }
        }

    }


    class PreUpdateJavaResourcesTaskAction(
        private val record: RegularFileProperty,
    ) : PolyfillAction<List<RegularFile>> {

        override fun onTaskConfigure(task: Task) {}

        override fun onExecute(artifact: Provider<List<RegularFile>>) {
            artifact.get().let { files ->
                record.get().asFile.writeText(JSON.toJSONString(files.map { it.asFile.absolutePath }))
            }
        }

    }

    abstract class GetManifestMergeReportTask : DefaultTask() {

        @get: InputFile
        abstract val report: RegularFileProperty

        @get: OutputFile
        abstract val record: RegularFileProperty

        @TaskAction
        fun onPrint() {
            record.get().asFile.writeText(report.get().asFile.readText())
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
            )
        }
    }

}
