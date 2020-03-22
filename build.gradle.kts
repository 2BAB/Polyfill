import me.xx2bab.polyfill.buildscript.BuildConfig.Versions.polyfillDevVersion

buildscript {

    // Set project ext values as the workaround to collect all values that can't be set in buildSrc,
    // because buildscript can not read anything from the scripts(buildSrc) that will be compiled
    // based on this buildscript
    project.extra["kotlinVersion"] = "1.3.70"
    project.extra["agpVersion"] = "3.6.1"
    project.extra["brpVersion"] = "0.9.1"

    repositories {
        google()
        jcenter()
        mavenLocal()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = project.extra["kotlinVersion"].toString()))
        classpath("com.android.tools.build:gradle:${project.extra["agpVersion"]}")
        classpath("com.novoda:bintray-release:${project.extra["brpVersion"]}")
    }

}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
    configPublish(this)
}

plugins {
    id("me.2bab.polyfill.build.release")
}

task("clean") {
    delete(rootProject.buildDir)
}


fun configPublish(p: Project) {
    if (p.name == "polyfill-parent") {
        return
    }

    p.group = "me.2bab"
    p.version = polyfillDevVersion

    p.apply(plugin = "com.novoda.bintray-release")

    val properties = java.util.Properties()
    val localPropertiesFile = p.rootProject.file("local.properties")
    var bintrayUserName = ""
    var bintrayApiKey = ""
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
        bintrayUserName = properties.getProperty("bintray.user")
        bintrayApiKey = properties.getProperty("bintray.apikey")
    } else {
        bintrayUserName = System.getenv("BINTRAY_USER")
        bintrayApiKey = System.getenv("BINTRAY_APIKEY")
    }

    require(!(bintrayUserName.isEmpty() || bintrayApiKey.isEmpty())) {
        "Please set user and apiKey for Bintray uploading."
    }

    p.configure<com.novoda.gradle.release.PublishExtension> {
        userOrg = "2bab"
        groupId = p.group as String
        artifactId = p.properties["ARTIFACT_ID"] as String
        publishVersion = polyfillDevVersion
        desc = "Hook Toolset for Android Gradle Plugin"
        website = "https://github.com/2BAB/Polyfill"
        bintrayUser = bintrayUserName
        bintrayKey = bintrayApiKey
    }
}