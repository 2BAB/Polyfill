import me.xx2bab.polyfill.*
import me.xx2bab.polyfill.manifest.source.ManifestMergeTaskListener
import com.alibaba.fastjson.*

project.afterEvaluate {
    val manifestTaskMergeTaskListener = ManifestMergeTaskListener()
    manifestTaskMergeTaskListener.beforeMerge { list ->
        val manifestPathsOutput = FunctionTestFixtures.getOutputFile(project, "manifest-merge-input.json")
        manifestPathsOutput.createNewFile()
        manifestPathsOutput.writeText(JSON.toJSONString(list.map { it.absolutePath }))
    }
    Polyfill(this).addOnAGPTaskListener(manifestTaskMergeTaskListener)
}