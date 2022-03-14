rootProject.name = "polyfill-func-test-project"
enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {

    val versions = file("../deps.versions.toml").readText()
    val regexPlaceHolder = "%s\\s\\=\\s\\\"([A-Za-z0-9\\.\\-]+)\\\""
    val getVersion = { s: String -> regexPlaceHolder.format(s).toRegex().find(versions)!!.groupValues[1] }

    plugins {
        id("com.android.application") version getVersion("agpVer") apply false
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
include(":app")

// Substitute the test plugin with a project(":test-plugin"),
// also check ./build.gradle.kts
includeBuild("../") {
    dependencySubstitution {
        substitute(module("me.2bab:polyfill-test-plugin"))
            .with(project(":test-plugin"))
    }
}
