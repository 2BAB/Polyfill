import me.xx2bab.polyfill.buildscript.BuildConfig.Path
import me.xx2bab.polyfill.buildscript.BuildConfig.Versions

buildscript {

    val props = java.util.Properties()
    file("./buildSrc/src/main/resources/versions.properties").inputStream().use { props.load(it) }

    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = props["kotlinVersion"]?.toString()))
    }

}

plugins {
    id("me.xx2bab.polyfill.buildscript.github-release")
}

allprojects {
    version = Versions.polyfillDevVersion
    group = "me.2bab"
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

task("clean") {
    delete(rootProject.buildDir)
}

val aggregateJars by tasks.registering {
    doLast {
        val output = Path.getAggregatedJarDirectory(project)
        output.mkdir()
        subprojects {
            File(buildDir.absolutePath + File.separator + "libs").walk()
                    .filter { it.name.startsWith("polyfill") && it.extension == "jar" }
                    .forEach { it.copyTo(File(output, it.name)) }
        }
    }
}
