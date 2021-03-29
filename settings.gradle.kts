rootProject.name = "polyfill-parent"

val polyfillPubModule = settings.startParameter.projectProperties["polyfillPublish"]

if (polyfillPubModule != null) {
    include(":$polyfillPubModule")
} else {
    include(":polyfill") // Main Entry
    include(":polyfill-agp") // Android Gradle Plugin relevant features
    include(":polyfill-gradle") // Gradle relevant features
    include(":polyfill-arsc") // resource.arsc relevant features
    include(":polyfill-manifest") // AndroidManifest relevant features
    include(":polyfill-matrix") // Tools, Extensions, Resources
    include(":test-plugin") // A test plugin that integrates polyfill
}