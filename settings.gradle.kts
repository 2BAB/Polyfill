rootProject.name = "polyfill-parent"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {

    val versions = file("deps.versions.toml").readText()
    val regexPlaceHolder = "%s\\s\\=\\s\\\"([A-Za-z0-9\\.\\-]+)\\\""
    val getVersion = { s: String -> regexPlaceHolder.format(s).toRegex().find(versions)!!.groupValues[1] }

    plugins {
        kotlin("jvm") version getVersion("kotlinVer")
        id("com.github.gmazzo.buildconfig") version getVersion("buildConfigVer") apply false
        kotlin("plugin.serialization") version getVersion("kotlinVer") apply false
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
include(":polyfill-backport")
include(":android-arsc-parser") // resource.arsc parser
include(":android-manifest-parser") // AndroidManifest.xml parser
include(":polyfill-test-plugin") // A test plugin for testing polyfill function
include(":functional-test")
