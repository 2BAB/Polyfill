import me.xx2bab.polyfill.buildscript.BuildConfig.Deps

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    google()
    mavenCentral()
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(project(":polyfill"))
    implementation(kotlin(Deps.ktStd))
    implementation(kotlin(Deps.ktReflect))
    implementation(Deps.fastJson)
    compileOnly(Deps.agp)
}

gradlePlugin {
    plugins.register("polyfill-test-plugin") {
        id = "polyfill-test-plugin"
        implementationClass = "me.xx2bab.polyfill.test.TestPlugin"
    }
}