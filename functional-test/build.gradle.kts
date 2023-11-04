plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "me.2bab"

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_17
}


testing {
    suites {
        val functionalTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()
            testType.set(TestSuiteType.FUNCTIONAL_TEST)
            dependencies {
                implementation(deps.hamcrest)
                implementation(deps.kotlin.serialization)
                implementation(deps.fastJson)
            }
        }
    }
}

dependencies {
    implementation(deps.kotlin.std)
    "functionalTestImplementation"(gradleTestKit())
}

tasks.named("check") {
    dependsOn(testing.suites.named("functionalTest"))
}

tasks.withType<Test> {
    testLogging {
        this.showStandardStreams = true
    }
}