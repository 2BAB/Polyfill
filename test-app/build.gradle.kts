buildscript {

    project.extra["kotlinVersion"] = "1.4.31"
    project.extra["agpVersion"] = "4.2.0-beta06"

    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = project.extra["kotlinVersion"].toString()))
        classpath("com.android.tools.build:gradle:${project.extra["agpVersion"]}")
        classpath("me.2bab:polyfill-test-plugin:+") // Will be replaced with project(":test-plugin") by includeBuild()
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