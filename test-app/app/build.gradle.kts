import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("polyfill-test-plugin")
}

val props = Properties()
file("../../buildSrc/src/main/resources/versions.properties").inputStream().use { props.load(it) }

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")
    defaultConfig {
        applicationId = "me.xx2bab.polyfill.sample"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = props["polyfillVersion"].toString()
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${props["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${props["kotlinVersion"]}")
}
