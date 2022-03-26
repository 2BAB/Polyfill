package me.xx2bab.polyfill

import com.android.build.api.artifact.Artifact
import com.android.build.api.artifact.ArtifactKind
import com.android.build.api.artifact.MultipleArtifact
import com.android.build.api.artifact.SingleArtifact
import org.gradle.api.file.Directory
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.file.RegularFile

/**
 * To define the plugin type that is associated with supported Artifact types.
 */
interface PolyfilledPluginType

/**
 * To indicate an Artifact can be used in Application module only.
 */
interface PolyfilledApplicationArtifact : PolyfilledPluginType

/**
 * To indicate an Artifact can be used in Library module only.
 */
interface PolyfilledLibraryArtifact : PolyfilledPluginType




/**
 * The polyfill version of [Artifact].
 */
abstract class PolyfilledArtifact<FileTypeT : FileSystemLocation>(val kind: ArtifactKind<FileTypeT>)

/**
 * The polyfill version of [SingleArtifact].
 */
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

/**
 * The polyfill version of [MultipleArtifact].
 */
sealed class PolyfilledMultipleArtifact<FileTypeT : FileSystemLocation,
        PluginTypeT : PolyfilledPluginType>(kind: ArtifactKind<FileTypeT>) :
    PolyfilledArtifact<FileTypeT>(kind) {

    object ALL_MANIFESTS :
        PolyfilledMultipleArtifact<RegularFile, PolyfilledApplicationArtifact>(ArtifactKind.FILE)

    object ALL_RESOURCES :
        PolyfilledMultipleArtifact<Directory, PolyfilledApplicationArtifact>(ArtifactKind.DIRECTORY)

    object ALL_JAVA_RES :
        PolyfilledMultipleArtifact<RegularFile, PolyfilledApplicationArtifact>(ArtifactKind.FILE)
}

