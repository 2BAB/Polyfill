import me.xx2bab.polyfill.buildscript.BuildConfig.Versions

plugins {
    kotlin("jvm")
    id("me.xx2bab.polyfill.buildscript.maven-central-publish")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))

    implementation(gradleApi())
    implementation(deps.kotlin.std)

    compileOnly(deps.android.gradle.plugin)
    api(deps.guava)

    testImplementation(deps.junit)
    testImplementation(deps.mockito)
    testImplementation(deps.mockitoInline)
}

java {
    withSourcesJar()
    sourceCompatibility = Versions.polyfillSourceCompatibilityVersion
    targetCompatibility = Versions.polyfillTargetCompatibilityVersion
}