buildscript {

    // Set project ext values as the workaround to collect all values that can't be set in buildSrc,
    // because buildscript can not read anything from the scripts(buildSrc) that will be compiled
    // based on this buildscript
    project.extra["kotlinVersion"] = "1.4.10"
    project.extra["agpVersion"] = "4.2.0-alpha11"
    project.extra["brpVersion"] = "0.9.2"

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
}

task("clean") {
    delete(rootProject.buildDir)
}


val aggregateJars by tasks.registering {
    doLast {
        val output = File(rootProject.buildDir.absolutePath + File.separator + "libs")
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
