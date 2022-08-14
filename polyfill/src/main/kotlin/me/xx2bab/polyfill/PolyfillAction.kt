package me.xx2bab.polyfill

import org.gradle.api.Task
import org.gradle.api.provider.Provider

interface PolyfillAction<ArtifactType> {

    fun onTaskConfigure(task: Task)

    fun onExecute(artifact: Provider<ArtifactType>)

}