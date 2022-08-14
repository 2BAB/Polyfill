plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "me.2bab"

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(deps.kotlin.std)
}

testing {
    suites {
        val functionalTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()
            testType.set(TestSuiteType.FUNCTIONAL_TEST)
            dependencies {
                implementation(deps.hamcrest)
                implementation("dev.gradleplugins:gradle-test-kit:7.4.1")
                implementation(deps.kotlin.coroutine)
                implementation(deps.kotlin.serialization)
                implementation(deps.fastJson)
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("functionalTest"))
}

tasks.withType<Test> {
    testLogging {
        this.showStandardStreams = true
    }
}