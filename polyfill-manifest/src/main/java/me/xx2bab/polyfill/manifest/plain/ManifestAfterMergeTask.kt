package me.xx2bab.polyfill.manifest.plain

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class ManifestAfterMergeTask: DefaultTask() {

    lateinit var afterMergeAction: (File) -> ByteArray

    @get:InputFile
    abstract val mergedManifest: RegularFileProperty

    @get:OutputFile
    abstract val updatedManifest: RegularFileProperty

    @TaskAction
    open fun afterMerge() {
        updatedManifest.get().asFile.writeBytes(afterMergeAction.invoke(mergedManifest.get().asFile))
    }
}