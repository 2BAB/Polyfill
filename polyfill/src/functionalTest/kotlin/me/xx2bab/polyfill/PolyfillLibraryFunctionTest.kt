package me.xx2bab.polyfill

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import me.xx2bab.polyfill.manifest.bytes.parser.ManifestBytesTweaker
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
            GradleRunner.create()
                    .forwardOutput()
                    .withPluginClasspath()
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
    fun manifestBeforeMergeTaskActionTest_FilterSuccessfully() {
        val out = File("${testProjectJsonOutputPath}/manifest-merge-input.json")
        Assert.assertTrue(out.exists())
        val list = JSON.parseObject(out.readText(), object : TypeReference<List<String>>() {})
        Assert.assertTrue(list.any { it.contains("appcompat") })
    }

    @Test
    fun manifestAfterMergeTaskActionTest_modifyAllowBackUpSuccessfully() {
        val extractedAndroidManifest = File(testProjectAppUnzipPath, "AndroidManifest.xml")
        Assert.assertTrue(extractedAndroidManifest.exists())
        val manifestBytesTweaker = ManifestBytesTweaker()
        manifestBytesTweaker.read(extractedAndroidManifest)
        val applicationTag = manifestBytesTweaker.getSpecifyStartTagBodyByName("application")
        Assert.assertNotNull(applicationTag)
        val value = manifestBytesTweaker.getAttrFromTagAttrs(applicationTag!!, "allowBackup")!!.data
        Assert.assertEquals(false.toInt(), value) // The core assert which we changed it to false
    }

}