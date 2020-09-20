import me.xx2bab.polyfill.buildscript.BuildConfig.Path

buildscript {

    // Set project ext values as the workaround to collect all values that can't be set in buildSrc,
    // because buildscript can not read anything from the scripts(buildSrc) that will be compiled
    // based on this buildscript
    project.extra["kotlinVersion"] = "1.4.10"
    project.extra["agpVersion"] = "4.2.0-alpha11"

    repositories {
        google()
        jcenter()
        mavenLocal()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = project.extra["kotlinVersion"].toString()))
        classpath("com.android.tools.build:gradle:${project.extra["agpVersion"]}")
    }

}

plugins {
    id("me.xx2bab.polyfill.buildscript.github-release")
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
}

task("clean") {
    delete(rootProject.buildDir)
}

// TODO: move task definition to buildSrc
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

val buildForFunctionTest by tasks.registering {
    val copy = this
    subprojects {
        copy.dependsOn(":${name}:assemble")
    }
    copy.finalizedBy(aggregateJars)
}
