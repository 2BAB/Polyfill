plugins {
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
    mavenCentral()
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.android.tools.build:gradle:4.2.0-alpha12")
    implementation ("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")

    // Github Release
    implementation("com.github.breadmoirai:github-release:2.2.12")
}