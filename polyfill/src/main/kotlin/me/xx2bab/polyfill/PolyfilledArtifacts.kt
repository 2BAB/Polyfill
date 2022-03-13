package me.xx2bab.polyfill

import com.android.build.api.artifact.ArtifactKind
import org.gradle.api.file.Directory
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.file.RegularFile

interface PolyfilledPluginType
interface PolyfilledApplicationArtifact : PolyfilledPluginType
interface PolyfilledLibraryArtifact : PolyfilledPluginType

abstract class PolyfilledArtifact<FileTypeT : FileSystemLocation>(val kind: ArtifactKind<FileTypeT>)

sealed class PolyfilledSingleArtifact<FileTypeT : FileSystemLocation,
        PluginTypeT : PolyfilledPluginType>(kind: ArtifactKind<FileTypeT>) :
    PolyfilledArtifact<FileTypeT>(kind) {

        // For MERGED_MANIFEST you can use
        // [com.android.build.api.artifact.SingleArtifact.MERGED_MANIFEST] directly.
        // object MERGED_MANIFEST :
        //     PolyfilledSingleArtifact<RegularFile, PolyfilledApplicationArtifact>(ArtifactKind.FILE)

    object MERGED_RESOURCES :
        PolyfilledSingleArtifact<Directory, PolyfilledApplicationArtifact>(ArtifactKind.DIRECTORY)
}

sealed class PolyfilledMultipleArtifact<FileTypeT : FileSystemLocation,
        PluginTypeT : PolyfilledPluginType>(kind: ArtifactKind<FileTypeT>) :
    PolyfilledArtifact<FileTypeT>(kind) {

    object ALL_MANIFESTS :
        PolyfilledMultipleArtifact<RegularFile, PolyfilledApplicationArtifact>(ArtifactKind.FILE)

    object ALL_RESOURCES :
        PolyfilledMultipleArtifact<Directory, PolyfilledApplicationArtifact>(ArtifactKind.DIRECTORY)
}

