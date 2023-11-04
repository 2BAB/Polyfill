plugins {
    id("com.android.application")
    kotlin("android")
    id("polyfill-test-plugin")
}

android {
    namespace = "me.xx2bab.polyfill.sample"
    defaultConfig {
        applicationId = "me.xx2bab.polyfill.sample"
        minSdk = 21
        targetSdk = 34
        compileSdkVersion = "android-34"
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
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
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation(projects.androidLib)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}