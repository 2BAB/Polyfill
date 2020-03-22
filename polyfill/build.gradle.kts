import me.xx2bab.polyfill.build.BuildConfig.Deps
import me.xx2bab.polyfill.build.BuildConfig.Versions

plugins {
    id("kotlin")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    implementation("com.android.tools.build:gradle:${rootProject.extra["agpVersion"]}")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.javassist:javassist:3.22.0-GA")
    implementation("com.alibaba:fastjson:1.1.71.android")

    testImplementation(Deps.junit)
    testImplementation(Deps.mockito)
    testImplementation(Deps.mockitoInline)
}

java {
    sourceCompatibility = Versions.polyfillSourceCompatibilityVersion
    targetCompatibility = Versions.polyfillTargetCompatibilityVersion
}
