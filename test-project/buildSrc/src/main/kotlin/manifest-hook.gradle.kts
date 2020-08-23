import me.xx2bab.polyfill.*
import me.xx2bab.polyfill.manifest.source.ManifestMergeTaskListener

project.afterEvaluate {
    val manifestTaskMergeTaskListener = ManifestMergeTaskListener()
    manifestTaskMergeTaskListener.beforeMerge {
        it.all {
            project.logger.error(it.absolutePath)
            true
        }
    }
    Polyfill(this).addOnAGPTaskListener(manifestTaskMergeTaskListener)
}