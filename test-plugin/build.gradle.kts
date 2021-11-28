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
    implementation(projects.polyfill)
    implementation(projects.polyfillManifest)
    implementation(projects.polyfillRes)
    implementation(deps.kotlin.std)
    implementation(deps.kotlin.reflect)
    implementation(deps.fastJson)
    compileOnly(deps.android.gradle.plugin)
}

gradlePlugin {
    plugins.register("polyfill-test-plugin") {
        id = "polyfill-test-plugin"
        implementationClass = "me.xx2bab.polyfill.test.TestPlugin"
    }
}