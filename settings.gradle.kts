rootProject.name = "polyfill-parent"

val polyfillPubModule = settings.startParameter.projectProperties["polyfillPublish"]

if (polyfillPubModule != null) {
    include(":$polyfillPubModule")
}

if (polyfillPubModule == null) {
    include(":polyfill") // Main Entry
    include(":polyfill-agp") // Android Gradle Plugin relevant features
    include(":polyfill-gradle") // Gradle relevant features
    include(":polyfill-arsc") // resource.arsc relevant features
    include(":polyfill-manifest") // Manifest relevant features
    include(":polyfill-matrix") // Tools, Extensions, Resources
}