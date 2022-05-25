package me.xx2bab.polyfill

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import net.lingala.zip4j.ZipFile
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.io.File

/**
 * To test all basic functions from polyfill libs including "AGPTaskAction" / "DataProvider" / etc.
 *
 * You can run this functional test from IDE or command line.
 * Use `functionalTest` or `check` gradle command to run all testing.
 *
 * All libs usage logic can be found from `rootProject/test-plugin/src/main/kotlin`.
 * All test related scripts can be found from `rootProject/scripts/`.
 */
class PolyfillLibraryFunctionTest {

    companion object {

        private const val testProjectPath = "../test-app"
        private const val testProjectJsonOutputPath = "${testProjectPath}/build/functionTestOutput"
        private const val testProjectAppOutputPath = "${testProjectPath}/app/build/outputs/apk/debug"
        private const val testProjectAppUnzipPath = "${testProjectPath}/app/build/outputs/apk/debug/unzipped"

        @BeforeClass
        @JvmStatic
        fun buildTestProject() {
            // Must run it after libs deployed
            println("Building...")
            File(testProjectJsonOutputPath).deleteRecursively()
            GradleRunner.create()
                    .forwardOutput()
                    .withArguments("clean", "assembleDebug", "--stacktrace")
                    .withProjectDir(File("../test-app"))
                    .build()

            println("Unzipping...")
            unzipApk()

            println("Testing...")
        }

        private fun unzipApk() {
            File(testProjectAppOutputPath)
                    .walk()
                    .filter { it.extension == "apk" }
                    .first {
                        val unzipFolder = File(testProjectAppUnzipPath)
                        if (!unzipFolder.exists()) {
                            unzipFolder.mkdir()
                            ZipFile(it.absolutePath).extractAll(unzipFolder.absolutePath)
                        }
                        true
                    }
        }

        fun Boolean.toInt() = if (this) 1 else 0
    }


    @Test
    fun manifestMergePreHookConfigureActionTest_TransformSuccessfully() {
        val out = File("${testProjectJsonOutputPath}/all-manifests-by-preHookManifestTaskAction2.json")
        Assert.assertTrue(out.exists())
        val list = JSON.parseObject(out.readText(), object : TypeReference<List<String>>() {})
        Assert.assertTrue(list.any { it.contains("appcompat") })
    }

    @Test
    fun manifestMergePreHookConfigureActionTest_GetSuccessfully() {
        val fileFromGetMethod = File("${testProjectJsonOutputPath}/all-manifests-by-getAllInputManifestsForDebug.json")
        Assert.assertTrue(fileFromGetMethod.exists())
        val getTaskDeps = File("${testProjectJsonOutputPath}/get-all-input-manifests-for-debug-task-deps.txt")
        Assert.assertEquals(getTaskDeps.readText(), "processDebugManifest")
    }

    @Test
    fun generateAllResourcesBeforeMergeTest_FetchSuccessfully() {
        val out = File("${testProjectJsonOutputPath}/all-resources.json")
        Assert.assertTrue(out.exists())
        val list = JSON.parseObject(out.readText(), object : TypeReference<List<String>>() {})
        Assert.assertTrue(list.any { it.contains("appcompat") })
    }

    @Test
    fun getResourceMergeDirTest_FetchSuccessfully() {
        val out = File("${testProjectJsonOutputPath}/merged-resource-dir.txt")
        Assert.assertTrue(out.exists())
        Assert.assertTrue(out.readText().contains("app/build/intermediates/merged_res/debug"))
    }

    @Test
    fun javaResourceMergePreHookConfigureAction_TransformSuccessfully() {
        val out = File("${testProjectJsonOutputPath}/all-java-res-by-preUpdateJavaResTaskAction.json")
        Assert.assertTrue(out.exists())
        Assert.assertTrue(out.readText().contains("android-lib/build/intermediates/library_java_res/debug/res.jar"))
    }

}