import me.xx2bab.polyfill.buildscript.BuildConfig.Deps
import me.xx2bab.polyfill.buildscript.BuildConfig.Versions

version = Versions.polyfillDevVersion

plugins {
    id("kotlin")
    id("me.xx2bab.polyfill.buildscript.bintray-publish")
    `java-gradle-plugin`
    `kotlin-dsl`
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

        val plusCollection = scopes["TEST"]?.get("plus")
        plusCollection?.addAll(funcTestImplementation.all.filter {
            it.name.contains("funcTestCompileClasspath")
                    || it.name.contains("funcTestRuntimeClasspath")
        })
    }
}

val functionTest by tasks.registering(Test::class) {
    dependsOn(project.parent!!.tasks.getByPath("buildForFunctionTest"))
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

    if(hasProperty("polyfillPublish")) {
        api("me.2bab:polyfill-arsc:${Versions.polyfillDevVersion}")
        api("me.2bab:polyfill-manifest:${Versions.polyfillDevVersion}")
        api("me.2bab:polyfill-gradle:${Versions.polyfillDevVersion}")
        api("me.2bab:polyfill-agp:${Versions.polyfillDevVersion}")
        api("me.2bab:polyfill-matrix:${Versions.polyfillDevVersion}")
    } else {
        api(project(":polyfill-arsc"))
        api(project(":polyfill-manifest"))
        api(project(":polyfill-gradle"))
        api(project(":polyfill-agp"))
        api(project(":polyfill-matrix"))
    }

    implementation(gradleApi())
    implementation(Deps.agp)
    implementation(kotlin(Deps.ktStd))
    implementation(kotlin(Deps.ktReflect))

    testImplementation(gradleTestKit())
    testImplementation(Deps.agp)
    testImplementation(Deps.junit)
    testImplementation(Deps.mockito)
    testImplementation(Deps.mockitoInline)
    testImplementation(Deps.fastJson)
    testImplementation(Deps.zip4j)
}

java {
    withSourcesJar()
    sourceCompatibility = Versions.polyfillSourceCompatibilityVersion
    targetCompatibility = Versions.polyfillTargetCompatibilityVersion
}
