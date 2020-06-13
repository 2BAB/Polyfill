package me.xx2bab.polyfill.buildscript

import org.gradle.api.JavaVersion

object BuildConfig {

    object Versions {
        const val polyfillDevVersion = "0.1.0.2-SNAPSHOT"

        val polyfillSourceCompatibilityVersion = JavaVersion.VERSION_1_7
        val polyfillTargetCompatibilityVersion = JavaVersion.VERSION_1_7
    }

    object Deps {
        // Test
        const val junit = "junit:junit:4.12"
        const val mockito = "org.mockito:mockito-core:3.3.1"
        const val mockitoInline = "org.mockito:mockito-inline:3.3.1"
    }

}