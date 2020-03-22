package me.xx2bab.polyfill.buildscript.release

import me.xx2bab.polyfill.buildscript.release.utils.Logger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class ReleaseTask : DefaultTask() {

    @TaskAction
    fun release() {
        Logger.i("Deploy Completed.");
    }

}