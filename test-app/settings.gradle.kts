rootProject.name = "polyfill-func-test-project"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {

    val versions = file("../deps.versions.toml").readText()
    val regexPlaceHolder = "%s\\s\\=\\s\\\"([A-Za-z0-9\\.\\-]+)\\\""
    val getVersion = { s: String -> regexPlaceHolder.format(s).toRegex().find(versions)!!.groupValues[1] }

    plugins {
        id("com.android.application") version getVersion("agpVer") apply false
        id("com.android.library") version getVersion("agpVer") apply false
        kotlin("android") version getVersion("kotlinVer")  apply false
    }
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "polyfill-test-plugin" -> useModule("me.2bab:polyfill-test-plugin:+")
            }
        }
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
    versionCatalogs {
        create("deps") {
            from(files("../deps.versions.toml"))
        }
    }
}

// Main test app
include(":app", ":android-lib")

// Substitute the test plugin with a project(":test-plugin"),
// also check ./build.gradle.kts
includeBuild("../") {
    dependencySubstitution {
        substitute(module("me.2bab:polyfill-test-plugin"))
            .with(project(":test-plugin"))
    }
}
