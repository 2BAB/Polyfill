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
    implementation("com.android.tools.build:gradle:4.1.0-rc01")
//    implementation("me.2bab:polyfill:test-version")
}

task("cleanLibs") {
//    dependsOn("clean")
    delete("./libs")
}