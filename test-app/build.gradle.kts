import java.util.*

buildscript {

    val props = java.util.Properties()
    file("../buildSrc/src/main/resources/versions.properties").inputStream().use { props.load(it) }

    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = props["kotlinVersion"]?.toString()))
        classpath("com.android.tools.build:gradle:${props["agpVersion"]}")

        // Will be replaced with project(":test-plugin") by includeBuild()
        classpath("me.2bab:polyfill-test-plugin:+")
    }

}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

task("clean") {
    delete(rootProject.buildDir)
}