plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "me.xx2bab.polyfill.sample.android"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets["main"].java.srcDir("src/main/kotlin")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}