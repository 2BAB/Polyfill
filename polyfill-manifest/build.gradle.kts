import me.xx2bab.polyfill.buildscript.BuildConfig.Deps
import me.xx2bab.polyfill.buildscript.BuildConfig.Versions

plugins {
    `java-gradle-plugin`
    id("kotlin")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    implementation(gradleApi())
    implementation("com.android.tools.build:gradle:${rootProject.extra["agpVersion"]}")
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":polyfill-arsc"))
    implementation(project(":polyfill-matrix"))

    testImplementation(Deps.junit)
    testImplementation(Deps.mockito)
    testImplementation(Deps.mockitoInline)
}

java {
    sourceCompatibility = Versions.polyfillSourceCompatibilityVersion
    targetCompatibility = Versions.polyfillTargetCompatibilityVersion
}


val functionalTestSourceSet = sourceSets.create("functionalTest")
gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations.getByName("functionalTestImplementation")
        .extendsFrom(configurations.getByName("testImplementation"))

// Add a task to run the functional tests
val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
}

val check by tasks.getting(Task::class) {
    // Run the functional tests as part of `check`
    dependsOn(functionalTest)
}