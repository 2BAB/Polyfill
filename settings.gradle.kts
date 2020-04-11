rootProject.name = "polyfill-parent"

val polyfillPubModule = settings.startParameter.projectProperties["polyfillPublish"]

if (polyfillPubModule != null) {
    include(":$polyfillPubModule")
}

if (polyfillPubModule == null) {
    include(":polyfill")
    include(":polyfill-arsc")
}