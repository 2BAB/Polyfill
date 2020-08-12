package me.xx2bab.polyfill.manifest

import org.gradle.testkit.runner.GradleRunner
import org.junit.Test
import java.io.File
import kotlin.test.junit.JUnitAsserter


class ManifestMergeTaskIntegrationTest {

    @Test
    fun manifestMergeProviderHookTest() {
        JUnitAsserter.assertEquals("1", "2", "2")
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("assembleDebug", "--stacktrace")
        runner.withProjectDir(File("/test-project"))

        runner.build()
    }

}