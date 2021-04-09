package me.xx2bab.polyfill.buildscript

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.io.File
import java.util.*

object BuildConfig {

    val props = Properties()

    init {
        javaClass.classLoader.getResourceAsStream("versions.properties")
            .use { props.load(it) }
    }

    object Path {
        fun getAggregatedJarDirectory(project: Project) = File(
                project.rootProject.buildDir.absolutePath + File.separator + "libs")
    }

    object Versions {
        val polyfillDevVersion by lazy { props["polyfillVersion"].toString() }

        val polyfillSourceCompatibilityVersion = JavaVersion.VERSION_1_8
        val polyfillTargetCompatibilityVersion = JavaVersion.VERSION_1_8
    }

    object Deps {
        const val ktStd = "stdlib-jdk8"
        const val ktReflect = "reflect"
        val agp by lazy { "com.android.tools.build:gradle:${props["agpVersion"]}" }

        const val fastJson = "com.alibaba:fastjson:1.2.73"
        const val zip4j = "net.lingala.zip4j:zip4j:2.6.2"

        // Test
        const val junit = "junit:junit:4.12"
        const val mockito = "org.mockito:mockito-core:3.9.0"
        const val mockitoInline = "org.mockito:mockito-inline:3.9.0"
    }

}