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
    implementation("com.android.tools.build:gradle:4.0.0")

    // Github Release
    implementation("gradle.plugin.com.github.breadmoirai:github-release:2.2.1")
    implementation("com.squareup.okhttp3:okhttp:3.8.1")
    implementation("com.j256.simplemagic:simplemagic:1.10")
    implementation("org.zeroturnaround:zt-exec:1.10")
}