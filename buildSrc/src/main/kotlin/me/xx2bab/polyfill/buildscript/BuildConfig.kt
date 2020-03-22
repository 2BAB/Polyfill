package me.xx2bab.polyfill.buildscript

import org.gradle.api.JavaVersion

object BuildConfig {

    object Versions {
        const val polyfillDevVersion = "0.1.0.1-SNAPSHOT"

        val polyfillSourceCompatibilityVersion = JavaVersion.VERSION_1_7
        val polyfillTargetCompatibilityVersion = JavaVersion.VERSION_1_7
    }

    object Deps {
        const val javapoet = "com.squareup:javapoet:1.11.1"
        const val fastjson = "com.alibaba:fastjson:1.1.71.android"
        const val orgJson = "org.json:json:20160212"
        const val junit = "junit:junit:4.12"
        const val mockito = "org.mockito:mockito-core:3.3.1"
        const val mockitoInline = "org.mockito:mockito-inline:3.3.1"
    }

}