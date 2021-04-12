import me.xx2bab.polyfill.buildscript.BuildConfig.Deps
import me.xx2bab.polyfill.buildscript.BuildConfig.Versions
import me.xx2bab.polyfill.buildscript.BuildConfig.props
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("kotlin")
    id("me.xx2bab.polyfill.buildscript.maven-central-publish")
    `java-gradle-plugin`
    `kotlin-dsl`
    idea
}

val fixtureClasspath: Configuration by configurations.creating
tasks.pluginUnderTestMetadata {
    pluginClasspath.from(fixtureClasspath)
}

val functionalTestSourceSet: SourceSet = sourceSets.create("functionalTest") {
    compileClasspath += sourceSets.main.get().output + configurations.testRuntimeClasspath
    runtimeClasspath += output + compileClasspath
}

val functionalTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

gradlePlugin.testSourceSets(functionalTestSourceSet)

idea {
    module {
        testSourceDirs = testSourceDirs.plus(functionalTestSourceSet.allSource.srcDirs)
        testResourceDirs = testResourceDirs.plus(functionalTestSourceSet.resources.srcDirs)

        val plusCollection = scopes["TEST"]?.get("plus")
        plusCollection?.addAll(functionalTestImplementation.all.filter {
            it.name.contains("funcTestCompileClasspath")
                    || it.name.contains("funcTestRuntimeClasspath")
        })
    }
}

val functionalTest by tasks.registering(Test::class) {
    failFast = true
    description = "Runs functional tests."
    group = "verification"
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
}

val check by tasks.getting(Task::class) {
    dependsOn(functionalTest)
}

val test by tasks.getting(Test::class) {
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
}

@Suppress("UnstableApiUsage")
val fixtureAgpVersion: String = providers
    .environmentVariable("AGP_VERSION")
    .forUseAtConfigurationTime()
    .orElse(providers.gradleProperty("agpVersion").forUseAtConfigurationTime())
    .getOrElse(props["agpVersion"].toString())


dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))

    api(project(":polyfill-arsc"))
    api(project(":polyfill-manifest"))
    api(project(":polyfill-gradle"))
    api(project(":polyfill-agp"))
    api(project(":polyfill-matrix"))

    implementation(gradleApi())
    implementation(kotlin(Deps.ktStd))
    implementation(kotlin(Deps.ktReflect))
    compileOnly(Deps.agp) // Let the test resource or user decide

    println(fixtureAgpVersion)
    functionalTestImplementation("com.android.tools.build:gradle:${fixtureAgpVersion}")
    fixtureClasspath("com.android.tools.build:gradle:${fixtureAgpVersion}")

    testImplementation(gradleTestKit())
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
