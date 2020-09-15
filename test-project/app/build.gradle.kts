plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")

    `env-init`
    `manifest-hook`
//    id("me.2bab.polyfill.test.manifest")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "me.xx2bab.polyfill.test"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "0.1.0"
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    sourceSets["main"].java.srcDir("src/main/kotlin")
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${rootProject.extra["kotlinVersion"].toString()}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${rootProject.extra["kotlinVersion"].toString()}")
}