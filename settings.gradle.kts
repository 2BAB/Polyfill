rootProject.name = "polyfill-parent"
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

include(":polyfill")
include(":android-arsc-parser") // resource.arsc parser
include(":android-manifest-parser") // AndroidManifest.xml parser
include(":test-plugin") // A test plugin for testing polyfill function
