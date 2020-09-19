import me.xx2bab.polyfill.manifest.source.ManifestBeforeMergeListener
import me.xx2bab.polyfill.manifest.source.ManifestAfterMergeListener
import com.alibaba.fastjson.*
import me.xx2bab.polyfill.manifest.source.ManifestMergeInputProvider
import me.xx2bab.polyfill.manifest.source.ManifestMergeOutputProvider

// 0. Gets Polyfill instance with Project instance
val polyfill = FunctionTestFixtures.getApplicationPolyfill(project)

// 1. Starts onVariantProperties
polyfill.onVariantProperties {
    val variant = this
    // 3. Create & Config the hook task.
    val preUpdateTask = project.tasks.register("preUpdate${variant.name.capitalize()}Manifest",
            ManifestBeforeMergeTask::class.java) {
        beforeMergeInputs.set(polyfill.getProvider(variant, ManifestMergeInputProvider::class.java).get())
    }
    // 4. Add it with the listener (which plays the role of entry for a hook).
    val beforeMergeListener = ManifestBeforeMergeListener(preUpdateTask)
    polyfill.addAGPTaskListener(variant, beforeMergeListener)


    // Let's try again with after merge hook
    val postUpdateTask = project.tasks.register("postUpdate${variant.name.capitalize()}Manifest",
            ManifestAfterMergeTask::class.java) {
        afterMergeInputs.set(polyfill.getProvider(variant, ManifestMergeOutputProvider::class.java).get())
    }
    val afterMergeListener = ManifestAfterMergeListener(postUpdateTask)
    polyfill.addAGPTaskListener(variant, afterMergeListener)
}

// 2. Prepare the task containing specific hook logic.
abstract class ManifestBeforeMergeTask : DefaultTask() {
    @get:InputFiles
    abstract val beforeMergeInputs: SetProperty<FileSystemLocation>

    @TaskAction
    fun beforeMerge() {
        val manifestPathsOutput = FunctionTestFixtures.getOutputFile(project, "manifest-merge-input.json")
        manifestPathsOutput.createNewFile()
        beforeMergeInputs.get().let { set ->
            manifestPathsOutput.writeText(JSON.toJSONString(set.map { it.asFile.absolutePath }))
        }
    }
}

abstract class ManifestAfterMergeTask : DefaultTask() {

    @get:InputFiles
    abstract val afterMergeInputs: RegularFileProperty

    @TaskAction
    fun afterMerge() {
        if (afterMergeInputs.isPresent) {
            val file = afterMergeInputs.get().asFile
            val modifiedManifest = file.readText()
                    .replace("allowBackup=\"true\"", "allowBackup=\"false\"")
            file.writeText(modifiedManifest)
        }
    }

}
