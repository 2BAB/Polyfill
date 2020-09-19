import me.xx2bab.polyfill.buildscript.BuildConfig.Deps
import me.xx2bab.polyfill.buildscript.BuildConfig.Versions

version = Versions.polyfillDevVersion

plugins {
    id("kotlin")
    id("me.xx2bab.polyfill.buildscript.bintray-publish")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    implementation(gradleApi())
    implementation(Deps.agp)
    implementation(kotlin(Deps.ktStd))

    if(hasProperty("polyfillPublish")) {
        implementation("me.2bab:polyfill-matrix:${Versions.polyfillDevVersion}")
    } else {
        implementation(project(":polyfill-matrix"))
    }

    testImplementation(Deps.junit)
    testImplementation(Deps.mockito)
    testImplementation(Deps.mockitoInline)
}

java {
    withSourcesJar()
    sourceCompatibility = Versions.polyfillSourceCompatibilityVersion
    targetCompatibility = Versions.polyfillTargetCompatibilityVersion
}
