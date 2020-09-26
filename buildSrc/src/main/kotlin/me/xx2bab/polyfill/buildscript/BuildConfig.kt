package me.xx2bab.polyfill.buildscript

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.io.File

object BuildConfig {

    object Path {

        fun getAggregatedJarDirectory(project: Project) = File(
                project.rootProject.buildDir.absolutePath + File.separator + "libs")

    }

    object Versions {
        const val polyfillDevVersion = "0.1.2"

        val polyfillSourceCompatibilityVersion = JavaVersion.VERSION_1_8
        val polyfillTargetCompatibilityVersion = JavaVersion.VERSION_1_8
    }

    object Deps {
        const val ktStd = "stdlib-jdk8"
        const val ktReflect = "reflect"
        const val agp = "com.android.tools.build:gradle:4.2.0-alpha12"

        // Test
        const val junit = "junit:junit:4.12"
        const val mockito = "org.mockito:mockito-core:3.3.1"
        const val mockitoInline = "org.mockito:mockito-inline:3.3.1"

        const val fastJson = "com.alibaba:fastjson:1.2.73"
        const val zip4j = "net.lingala.zip4j:zip4j:2.6.2"
    }

}