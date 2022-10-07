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

dependencies {
    implementation(kotlin("stdlib"))

    // Github Release
    implementation("com.github.breadmoirai:github-release:2.4.1")
}