package me.xx2bab.polyfill

import org.gradle.testkit.runner.GradleRunner
import org.junit.BeforeClass
import org.junit.Test
import java.io.File


/**
 * To test all basic functions from polyfill libs including "TaskListener" / "DataProvider" / etc.
 *
 * Currently this function test sometime can not run from the IDE somehow,
 * not sure what settings is missing. As the workaround, we run from command line,
 * and used "check" gradle command to run all testing.
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
        println("Testing...")
    }

    @Test
    fun manifestMergeDataProviderTest_success() {

    }


}