package me.xx2bab.koncat

import org.gradle.testkit.runner.GradleRunner
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.core.StringContains
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit

class SampleProjectTest {

    companion object {

        private const val baseTestProjectPath = "../test-app"
        private const val testProjectJsonOutputPath = "build/functionTestOutput"
        private const val testProjectAppOutputPath = "app/build/outputs/apk/debug"
        private const val testProjectAppUnzipPath = "app/build/outputs/apk/debug/unzipped"


        @BeforeAll
        @JvmStatic
        fun setup() {
            // TODO: run each of them in parallel (will require using remote dependency)
            agpVerProvider().forEach { buildTestProject(it) }
        }

        private fun buildTestProject(agpVer: String) {
            println("Publishing libraries to MavenLocal...")
            ("./gradlew" +
                    " :android-arsc-parser:publishAllPublicationsToMyMavenlocalRepository" +
                    " :android-manifest-parser:publishAllPublicationsToMyMavenlocalRepository" +
                    " :polyfill-backport:publishAllPublicationsToMyMavenlocalRepository" +
                    " :polyfill:publishAllPublicationsToMyMavenlocalRepository" +
                    " :polyfill-test-plugin:publishAllPublicationsToMyMavenlocalRepository" +
                    " --stacktrace"
                    ).runCommand(File("../"))
            println("All libraries published.")

            println(
                "Copying project for AGP [${agpVer}] from ${
                    File(
                        baseTestProjectPath
                    ).absolutePath
                }..."
            )
            val targetProject = File("./build/test-app-for-$agpVer")
            targetProject.deleteRecursively()
            File(baseTestProjectPath).copyRecursively(targetProject)
            val settings = File(targetProject, "settings.gradle.kts")
            val newSettings = settings.readText()
                .replace(
                    "= \"../\"",
                    "= \"../../../\""
                ) // Redirect the base dir
                .replace(
                    "enabledCompositionBuild = true",
                    "enabledCompositionBuild = false"
                ) // Force the app to find plugin from maven local
                .replace(
                    "getVersion(\"agpVer\")",
                    "\"$agpVer\""
                ) // Hardcode agp version
            settings.writeText(newSettings)

            println("assembleDebug test-app for [$agpVer]")
            GradleRunner.create().apply {
                forwardOutput()
                withProjectDir(targetProject)
                withGradleVersion("7.4.2")
                withArguments("clean", "assembleDebug", "--stacktrace")
                // Ensure this value is true when `--debug-jvm` is passed to Gradle, and false otherwise
                withDebug(
                    ManagementFactory.getRuntimeMXBean().inputArguments.toString()
                        .indexOf("-agentlib:jdwp") > 0
                )
                build()
            }
            println("Built test-app for [${agpVer}] successfully.")
        }

        @JvmStatic
        fun agpVerProvider(): List<String> {
            val versions = File("../deps.versions.toml").readText()
            val regexPlaceHolder = "%s\\s\\=\\s\\\"([A-Za-z0-9\\.\\-]+)\\\""
            val getVersion = { s: String ->
                regexPlaceHolder.format(s).toRegex().find(versions)!!.groupValues[1]
            }
            return listOf(
                getVersion("agpVer"),
                getVersion("agpBackportVer"),
//                getVersion("agpNextBetaVer")
            )
        }

        fun String.runCommand(workingDir: File) {
            ProcessBuilder(*split(" ").toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor(15, TimeUnit.MINUTES)
        }
    }


    @ParameterizedTest
    @MethodSource("agpVerProvider")
    fun buildToolInfoTest_getAAPT2Successfully(agpVer: String) {
        val out = File(
            "./build/test-app-for-$agpVer/" +
                    "${testProjectJsonOutputPath}/aapt2-path.txt"
        )
        assertThat("aapt2-path.txt does not exist", out.exists())
        assertThat(out.readText(), StringContains("sdk/build-tools"))
        assertThat(out.readText(), StringContains("/aapt2"))
    }

    @ParameterizedTest
    @MethodSource("agpVerProvider")
    fun manifestMergePreHookConfigureActionTest_TransformSuccessfully(agpVer: String) {
        val out = File(
            "./build/test-app-for-$agpVer/" +
                    "${testProjectJsonOutputPath}/all-manifests-by-preHookManifestTaskAction2.json"
        )
        assertThat("all-manifests-by-preHookManifestTaskAction2.json does not exist", out.exists())
        assertThat(out.readText(), StringContains("appcompat"))
    }

    @ParameterizedTest
    @MethodSource("agpVerProvider")
    fun manifestMergePreHookConfigureActionTest_GetSuccessfully(agpVer: String) {
        val fileFromGetMethod =
            File("./build/test-app-for-$agpVer/${testProjectJsonOutputPath}/all-manifests-by-getAllInputManifestsForDebug.json")
        assertThat("all-manifests-by-getAllInputManifestsForDebug.json does not exist", fileFromGetMethod.exists())
        val getTaskDeps =
            File("./build/test-app-for-$agpVer/${testProjectJsonOutputPath}/get-all-input-manifests-for-debug-task-deps.txt")
        assertThat(getTaskDeps.readText(), Matchers.equalTo("processDebugManifest"))
    }

    @ParameterizedTest
    @MethodSource("agpVerProvider")
    fun generateAllResourcesBeforeMergeTest_FetchSuccessfully(agpVer: String) {
        val out = File("./build/test-app-for-$agpVer/${testProjectJsonOutputPath}/all-resources.json")
        assertThat("all-resources.json does not exist", out.exists())
        assertThat(out.readText(), StringContains("appcompat"))
    }

    @ParameterizedTest
    @MethodSource("agpVerProvider")
    fun getResourceMergeDirTest_FetchSuccessfully(agpVer: String) {
        val out = File("./build/test-app-for-$agpVer/${testProjectJsonOutputPath}/merged-resource-dir.txt")
        assertThat("merged-resource-dir.txt does not exist", out.exists())
        assertThat(out.readText(), StringContains("app/build/intermediates/merged_res/debug"))
    }

    @ParameterizedTest
    @MethodSource("agpVerProvider")
    fun javaResourceMergePreHookConfigureAction_TransformSuccessfully(agpVer: String) {
        val out =
            File("./build/test-app-for-$agpVer/${testProjectJsonOutputPath}/all-java-res-by-preUpdateJavaResTaskAction.json")
        assertThat("all-java-res-by-preUpdateJavaResTaskAction.json does not exist", out.exists())
        assertThat(out.readText(), StringContains("android-lib/build/intermediates/library_java_res/debug/res.jar"))
    }


}