import me.xx2bab.polyfill.buildscript.BuildConfig.Deps
import me.xx2bab.polyfill.buildscript.BuildConfig.Versions

plugins {
    id("kotlin")
    `java-gradle-plugin`
    idea
}

val funcTestSourceSet: SourceSet = sourceSets.create("funcTest") {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

val funcTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

gradlePlugin.testSourceSets(funcTestSourceSet)

idea {
    module {
        testSourceDirs = testSourceDirs.plus(funcTestSourceSet.allSource.srcDirs)
        testResourceDirs = testResourceDirs.plus(funcTestSourceSet.resources.srcDirs)
//        val plusCollection = scopes["TEST"]?.get("plus")
//        plusCollection?.addAll(intTestImplementation.all)
    }
}

val functionTest by tasks.registering(Test::class) {
    description = "Runs function tests."
    group = "verification"
    testClassesDirs = funcTestSourceSet.output.classesDirs
    classpath = funcTestSourceSet.runtimeClasspath
}

val check by tasks.getting(Task::class) {
    dependsOn(functionTest)
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    implementation(project(":polyfill-arsc"))
    implementation(project(":polyfill-manifest"))
    implementation(project(":polyfill-gradle"))
    implementation(project(":polyfill-agp"))
    implementation(project(":polyfill-matrix"))

    implementation(gradleApi())
    implementation(Deps.agp)
    implementation(kotlin(Deps.ktStd))
    implementation(kotlin(Deps.ktReflect))

    testImplementation(gradleTestKit())
    testImplementation(Deps.agp)
    testImplementation(Deps.junit)
    testImplementation(Deps.mockito)
    testImplementation(Deps.mockitoInline)
}

java {
    sourceCompatibility = Versions.polyfillSourceCompatibilityVersion
    targetCompatibility = Versions.polyfillTargetCompatibilityVersion
}
