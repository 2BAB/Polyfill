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
        const val polyfillDevVersion = "0.9.1"

        val polyfillSourceCompatibilityVersion = JavaVersion.VERSION_11
        val polyfillTargetCompatibilityVersion = JavaVersion.VERSION_17
    }

}