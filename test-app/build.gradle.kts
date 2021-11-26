buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        // classpath(kotlin("gradle-plugin", version = props["kotlinVersion"]?.toString()))
        classpath("com.android.tools.build:gradle:7.0.3")

        // Will be replaced with project(":test-plugin") by includeBuild()
        classpath("me.2bab:polyfill-test-plugin:+")
    }
}

task("clean") {
    delete(rootProject.buildDir)
}
