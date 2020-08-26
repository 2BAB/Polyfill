package me.xx2bab.polyfill

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.io.File

/**
 * To test all basic functions from polyfill libs including "TaskListener" / "DataProvider" / etc.
 *
 * Currently this function test sometime can not run from the IDE somehow,
 * not sure what settings is missing. As the workaround, we run ot from command line,
 * and use `functionTest` or `check` gradle command to run all testing.
 *
 * If you want to trigger this test manually from IDE,
 * please run `./gradlew clean buildForFunctionTest` before you start.
 *
 * All libs usage logic can be found from `rootProject/test-project/buildSrc/src/main/kotlin`.
 * All test related scripts can be found from `rootProject/scripts/`.
 */
class PolyfillLibraryFunctionTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun buildTestProject() {
            // Must run it after libs deployed
            println("Released polyfill to ./buildSrc/libs.")
            println("Building...")
            GradleRunner.create()
                    .forwardOutput()
                    .withPluginClasspath()
                    .withArguments("clean", "assembleDebug", "--stacktrace")
                    .withProjectDir(File("../test-project"))
                    .build()

            println("Testing...")
        }

    }

    @Test
    fun manifestBeforeMergeTaskListenerTest_FilterSuccessfully() {
        val out = File("../test-project/build/functionTestOutput/manifest-merge-input.json")
        Assert.assertTrue(out.exists())
        val list = JSON.parseObject(out.readText(), object: TypeReference<List<String>>(){})
        Assert.assertTrue(list.any { it.contains("appcompat") })
    }

    @Test
    fun manifestMergeDataProviderTest_success() {

    }


}