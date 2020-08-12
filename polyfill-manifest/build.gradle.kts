import me.xx2bab.polyfill.buildscript.BuildConfig.Deps
import me.xx2bab.polyfill.buildscript.BuildConfig.Versions

plugins {
    `java-gradle-plugin`
    id("kotlin")
    idea
}

val integrationSourceSet = sourceSets.create("intTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
}


val intTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

configurations["intTestImplementation"].extendsFrom(configurations.testImplementation.get())

//val functionalTestSourceSet = sourceSets.getByName("functionalTest")
gradlePlugin.testSourceSets(integrationSourceSet)
//configurations.getByName("functionalTestImplementation")
//        .extendsFrom(configurations.testImplementation.get())

idea {
    module {
        testSourceDirs = integrationSourceSet.allSource.srcDirs
        testResourceDirs = integrationSourceSet.resources.srcDirs
//        scopes.TEST.plus += [ configurations.integrationTestCompile ]
    }
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

    testImplementation("com.android.tools.build:gradle:4.1.0-beta05")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

java {
    sourceCompatibility = Versions.polyfillSourceCompatibilityVersion
    targetCompatibility = Versions.polyfillTargetCompatibilityVersion
}

// Add a task to run the integration tests
val integrationTest by tasks.registering(Test::class) {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = integrationSourceSet.output.classesDirs
    classpath = integrationSourceSet.runtimeClasspath
}

val check by tasks.getting(Task::class) {
    // Run the integration tests as part of `check`
    dependsOn(integrationTest)
}
