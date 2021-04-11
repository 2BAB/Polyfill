rootProject.name = "polyfill-parent"

include(":polyfill") // Main Entry
include(":polyfill-agp") // Android Gradle Plugin relevant features
include(":polyfill-gradle") // Gradle relevant features
include(":polyfill-arsc") // resource.arsc relevant features
include(":polyfill-manifest") // AndroidManifest relevant features
include(":polyfill-matrix") // Tools, Extensions, Resources
include(":test-plugin") // A test plugin that integrates polyfill
