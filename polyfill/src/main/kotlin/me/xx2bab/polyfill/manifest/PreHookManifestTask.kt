package me.xx2bab.polyfill.manifest

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles

abstract class PreHookManifestTask: DefaultTask() {

    @get:InputFiles
    abstract val inputManifests: ListProperty<RegularFile>

}