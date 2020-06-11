rootProject.name = "polyfill-parent"

val polyfillPubModule = settings.startParameter.projectProperties["polyfillPublish"]

if (polyfillPubModule != null) {
    include(":$polyfillPubModule")
}

if (polyfillPubModule == null) {
    include(":polyfill")
    include(":polyfill-agp")
    include(":polyfill-gradle")
    include(":polyfill-arsc")
    include(":polyfill-manifest")
}