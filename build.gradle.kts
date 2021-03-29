import me.xx2bab.polyfill.buildscript.BuildConfig.Path

buildscript {

    val props = java.util.Properties()
    file("./versions.properties").inputStream().use { props.load(it) }

    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = props["kotlinVersion"]?.toString()))
        classpath("com.android.tools.build:gradle:${props["agpVersion"]}")
    }

}

plugins {
    id("me.xx2bab.polyfill.buildscript.github-release")
}

allprojects {
    repositories {
        google()
        mavenCentral()
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
