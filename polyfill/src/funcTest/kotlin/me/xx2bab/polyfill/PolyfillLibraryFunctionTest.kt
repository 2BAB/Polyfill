package me.xx2bab.polyfill

import org.gradle.testkit.runner.GradleRunner
import org.junit.BeforeClass
import org.junit.Test
import java.io.File

class PolyfillLibraryFunctionTest {

    @BeforeClass
    fun buildTestProject() {
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("assembleDebug", "clean", "--stacktrace")
        runner.withProjectDir(File("/test-project"))

        runner.build()
    }

    @Test
    fun manifestBeforeMergeTaskListenerTest_FilterSuccessfully() {

    }

    @Test
    fun manifestMergeDataProviderTest_success() {

    }



}