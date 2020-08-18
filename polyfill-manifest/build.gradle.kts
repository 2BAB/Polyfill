import me.xx2bab.polyfill.buildscript.BuildConfig.Deps
import me.xx2bab.polyfill.buildscript.BuildConfig.Versions

plugins {
    id("kotlin")
    `java-gradle-plugin`
    idea
}

val integrationSourceSet: SourceSet = sourceSets.create("intTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
}

val intTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

gradlePlugin.testSourceSets(integrationSourceSet)

idea {
    module {
        testSourceDirs = testSourceDirs.plus(integrationSourceSet.allSource.srcDirs)
        testResourceDirs = testResourceDirs.plus(integrationSourceSet.resources.srcDirs)
//        val plusCollection = scopes["TEST"]?.get("plus")
//        plusCollection?.addAll(intTestImplementation.all)
    }
}

val integrationTest by tasks.registering(Test::class) {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = integrationSourceSet.output.classesDirs
    classpath = integrationSourceSet.runtimeClasspath
}

val check by tasks.getting(Task::class) {
    dependsOn(integrationTest)
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    implementation(gradleApi())
    implementation(Deps.agp)
    implementation(kotlin(Deps.ktStd))

    implementation(project(":polyfill-arsc"))
    implementation(project(":polyfill-matrix"))

    testImplementation(Deps.agp)
    testImplementation(Deps.junit)
    testImplementation(Deps.mockito)
    testImplementation(Deps.mockitoInline)
}

java {
    sourceCompatibility = Versions.polyfillSourceCompatibilityVersion
    targetCompatibility = Versions.polyfillTargetCompatibilityVersion
}


