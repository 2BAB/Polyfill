package me.xx2bab.polyfill

import org.gradle.testkit.runner.GradleRunner
import org.junit.BeforeClass
import org.junit.Test
import java.io.File

class PolyfillLibraryFunctionTest {

    @BeforeClass
    fun buildTestProject() {
        println("Preparing...")
        GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withArguments("clean", "testBuild", "--stacktrace")
                .withProjectDir(File("/test-project"))

        println("Released polyfill to ./buildSrc/libs.")
        println("Building...")
        GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withArguments("clean", "assembleDebug", "cleanLibs", "--stacktrace")
                .withProjectDir(File("/test-project"))
                .build()

        println("Testing...")
    }

    @Test
    fun manifestBeforeMergeTaskListenerTest_FilterSuccessfully() {

    }

    @Test
    fun manifestMergeDataProviderTest_success() {

    }


}