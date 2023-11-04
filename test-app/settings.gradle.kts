rootProject.name = "polyfill-func-test-project"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

val externalDependencyBaseDir = extra["externalDependencyBaseDir"].toString()
val enabledCompositionBuild = true

pluginManagement {
    extra["externalDependencyBaseDir"] = "../"
    val versions = file(extra["externalDependencyBaseDir"].toString() + "deps.versions.toml").readText()
    val regexPlaceHolder = "%s\\s\\=\\s\\\"([A-Za-z0-9\\.\\-]+)\\\""
    val getVersion = { s: String -> regexPlaceHolder.format(s).toRegex().find(versions)!!.groupValues[1] }

    plugins {
        id("com.android.application") version getVersion("agpVer") apply false
        id("com.android.library") version getVersion("agpVer") apply false
        kotlin("android") version getVersion("kotlinVer")  apply false
    }
    repositories {
        mavenLocal()
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
        mavenLocal()
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("deps") {
            from(files(externalDependencyBaseDir + "deps.versions.toml"))
        }
    }
}

// Main test app
include(":app", ":android-lib")

// Substitute the test plugin with a project(":polyfill-test-plugin"),
// also check ./build.gradle.kts
if (enabledCompositionBuild) {
    includeBuild(externalDependencyBaseDir) {
        dependencySubstitution {
            substitute(module("me.2bab:polyfill-test-plugin"))
                .using(project(":polyfill-test-plugin"))
        }
    }
}
