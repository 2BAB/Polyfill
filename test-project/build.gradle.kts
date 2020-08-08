buildscript {

    project.extra["kotlinVersion"] = "1.3.72"
    project.extra["agpVersion"] = "4.1.0-beta05"
    project.extra["brpVersion"] = "0.9.2"

    repositories {
        google()
        jcenter()
        mavenLocal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = project.extra["kotlinVersion"].toString()))
        classpath("com.android.tools.build:gradle:${project.extra["agpVersion"]}")
        classpath("com.novoda:bintray-release:${project.extra["brpVersion"]}")
    }

}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
}

task("clean") {
    delete(rootProject.buildDir)
}


plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}
android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId "me.xx2bab.polyfill.test"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode 1
        versionName "0.1.0"
    }
    buildTypes {
        debug {
            minifyEnabled false
        }
    }
}
dependencies {
    implementation fileTree (dir: 'libs', include: ['*.jar'])
    implementation"org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72"
}