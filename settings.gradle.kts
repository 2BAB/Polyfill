rootProject.name = "polyfill-parent"
enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {

    val versions = file("deps.versions.toml").readText()
    val regexPlaceHolder = "%s\\s\\=\\s\\\"([A-Za-z0-9\\.\\-]+)\\\""
    val getVersion = { s: String -> regexPlaceHolder.format(s).toRegex().find(versions)!!.groupValues[1] }

    plugins {
        kotlin("jvm") version getVersion("kotlinVer")
    }
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
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
            from(files("./deps.versions.toml"))
        }
    }
}

include(":polyfill") // Main entry
include(":polyfill-agp") // Android Gradle Plugin basic features
include(":polyfill-gradle") // Gradle relevant features
include(":polyfill-arsc") // resource.arsc relevant features
include(":polyfill-res") // Original resources(image/xml/raw/assets) relevant features
include(":polyfill-manifest") // AndroidManifest.xml(text/binary) relevant features
include(":polyfill-matrix") // Tools, extensions, interfaces
include(":test-plugin") // A test plugin for testing polyfill function