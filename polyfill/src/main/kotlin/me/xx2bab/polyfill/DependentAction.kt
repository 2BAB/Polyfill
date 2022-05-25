package me.xx2bab.polyfill

import org.gradle.api.provider.Provider

interface DependentAction<ArtifactType> {

    fun getDependentFiles(): List<Any>

    fun onExecute(artifact: Provider<ArtifactType>)

}