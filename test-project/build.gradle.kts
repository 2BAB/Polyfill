buildscript {

    project.extra["kotlinVersion"] = "1.4.10"
    project.extra["agpVersion"] = "4.2.0-alpha12"

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