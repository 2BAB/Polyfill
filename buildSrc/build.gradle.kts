import java.util.*

plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }
}

val props = Properties()
file("./src/main/resources/versions.properties").inputStream().use { props.load(it) }
dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.android.tools.build:gradle:${props["agpVersion"]}")

    // Github Release
    implementation("com.github.breadmoirai:github-release:2.2.12")
}