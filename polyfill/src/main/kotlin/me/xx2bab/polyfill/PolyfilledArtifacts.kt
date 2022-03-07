package me.xx2bab.polyfill

import com.android.build.api.artifact.ArtifactKind
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.file.RegularFile

interface PolyfilledPluginType
interface PolyfilledApplicationArtifact : PolyfilledPluginType
interface PolyfilledLibraryArtifact : PolyfilledPluginType

abstract class PolyfilledArtifact<FileTypeT : FileSystemLocation>(val kind: ArtifactKind<FileTypeT>)
sealed class PolyfilledSingleArtifact<FileTypeT : FileSystemLocation,
        PluginTypeT : PolyfilledPluginType>(kind: ArtifactKind<FileTypeT>) :
    PolyfilledArtifact<FileTypeT>(kind)

sealed class PolyfilledMultipleArtifact<FileTypeT : FileSystemLocation,
        PluginTypeT : PolyfilledPluginType>(kind: ArtifactKind<FileTypeT>) :
    PolyfilledArtifact<FileTypeT>(kind)


object ManifestCollection : PolyfilledMultipleArtifact<RegularFile, PolyfilledApplicationArtifact>(ArtifactKind.FILE)
object ResourceCollection : PolyfilledMultipleArtifact<RegularFile, PolyfilledApplicationArtifact>(ArtifactKind.FILE)