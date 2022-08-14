plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("me.xx2bab.polyfill.buildscript.maven-central-publish")
}

repositories {
    google()
    mavenCentral()
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(deps.kotlin.std)
    implementation(deps.kotlin.reflect)
    implementation(deps.fastJson)

    compileOnly(deps.android.gradle.plugin)
    compileOnly(deps.android.tools.sdklib)
    implementation(projects.polyfill)
}

gradlePlugin {
    plugins.register("polyfill-test-plugin") {
        id = "polyfill-test-plugin"
        implementationClass = "me.xx2bab.polyfill.test.TestPlugin"
    }
}