rootProject.name = "polyfill-func-test-project"

// Main test app
include(":app")

// Substitute the test plugin with a project(":test-plugin"),
// also check ./build.gradle.kts
includeBuild("../") {
    dependencySubstitution {
        substitute(module("me.2bab:polyfill-test-plugin"))
            .with(project(":test-plugin"))
    }
}
