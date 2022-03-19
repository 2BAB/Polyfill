import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    id("polyfill-test-plugin")
}

android {
    compileSdkVersion(31)
    defaultConfig {
        applicationId = "me.xx2bab.polyfill.sample"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
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
    implementation(deps.kotlin.std)
}
