
import me.xx2bab.polyfill.buildscript.BuildConfig.Versions

plugins {
    kotlin("jvm")
    id("me.xx2bab.polyfill.buildscript.maven-central-publish")
    id("me.xx2bab.polyfill.buildscript.functional-test-setup")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    api(projects.polyfillGradle)
    api(projects.polyfillAgp)
    api(projects.polyfillMatrix)

    implementation(gradleApi())
    implementation(deps.kotlin.std)
    implementation(deps.kotlin.reflect)

    // Let the test resource or user decide
    compileOnly(deps.android.gradle.plugin)
    compileOnly(deps.android.tools.common)

    testImplementation(gradleTestKit())
    testImplementation(deps.junit)
    testImplementation(deps.mockito)
    testImplementation(deps.mockitoInline)
    testImplementation(deps.fastJson)
    testImplementation(deps.zip4j)

    // For functionalTestImplementation
    // please refer to the functional-test-setup script
}

java {
    withSourcesJar()
    sourceCompatibility = Versions.polyfillSourceCompatibilityVersion
    targetCompatibility = Versions.polyfillTargetCompatibilityVersion
}