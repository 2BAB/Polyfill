
import me.xx2bab.polyfill.buildscript.BuildConfig.Deps
import me.xx2bab.polyfill.buildscript.BuildConfig.Versions

plugins {
    id("kotlin")
    id("me.xx2bab.polyfill.buildscript.maven-central-publish")
    id("me.xx2bab.polyfill.buildscript.functional-test-setup")

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))

    api(project(":polyfill-gradle"))
    api(project(":polyfill-agp"))
    api(project(":polyfill-matrix"))

    implementation(gradleApi())
    implementation(kotlin(Deps.ktStd))
    implementation(kotlin(Deps.ktReflect))
    compileOnly(Deps.agp) // Let the test resource or user decide
    compileOnly(Deps.androidToolsCommon)

    testImplementation(gradleTestKit())
    testImplementation(Deps.junit)
    testImplementation(Deps.mockito)
    testImplementation(Deps.mockitoInline)
    testImplementation(Deps.fastJson)
    testImplementation(Deps.zip4j)

    // For functionalTestImplementation
    // please refer to the functional-test-setup script
}

java {
    withSourcesJar()
    sourceCompatibility = Versions.polyfillSourceCompatibilityVersion
    targetCompatibility = Versions.polyfillTargetCompatibilityVersion
}
