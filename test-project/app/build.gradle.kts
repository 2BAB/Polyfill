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