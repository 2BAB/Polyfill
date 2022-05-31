package me.xx2bab.polyfill.artifact

import com.android.build.api.artifact.ArtifactKind
import com.android.build.api.variant.Variant
import com.android.build.api.variant.VariantExtension
import me.xx2bab.polyfill.*
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

/**
 * For all artifacts management.
 */
abstract class DefaultArtifactsRepository<PluginTypeT : PolyfilledPluginType>(
    private val project: Project,
    private val variant: Variant
) : ArtifactsRepository<PluginTypeT>, VariantExtension {

    private val singleArtifactStorage = mutableMapOf<PolyfilledArtifact<*>, SingleArtifactContainer<*>>()
    private val multipleArtifactStorage = mutableMapOf<PolyfilledArtifact<*>, MultipleArtifactContainer<*>>()

    init {
        val ext = project.extensions.getByType(PolyfillExtension::class.java)

        ext.singleArtifactMap.forEach { (artifactType, _) ->
            if (artifactType.kind == ArtifactKind.FILE) {
                singleArtifactStorage[artifactType] = SingleArtifactContainer<RegularFile>(
                    artifactType, project, variant
                )
            } else if (artifactType.kind == ArtifactKind.DIRECTORY) {
                singleArtifactStorage[artifactType] = SingleArtifactContainer<Directory>(
                    artifactType, project, variant
                )
            }
        }

        ext.multipleArtifactMap.forEach { (artifactType, _) ->
            if (artifactType.kind == ArtifactKind.FILE) {
                multipleArtifactStorage[artifactType] = MultipleArtifactContainer<RegularFile>(
                    artifactType, project, variant
                )
            } else if (artifactType.kind == ArtifactKind.DIRECTORY) {
                multipleArtifactStorage[artifactType] = MultipleArtifactContainer<Directory>(
                    artifactType, project, variant
                )
            }
        }
    }


    override fun <FileTypeT : FileSystemLocation> get(
        type: PolyfilledSingleArtifact<FileTypeT, PluginTypeT>,
    ): Provider<FileTypeT> = getSingleArtifactContainer(type).get()

    override fun <FileTypeT : FileSystemLocation> getAll(
        type: PolyfilledMultipleArtifact<FileTypeT, PluginTypeT>
    ): Provider<List<FileTypeT>> = getMultipleArtifactContainer(type).get()

    override fun <FileTypeT : FileSystemLocation> use(
        action: PolyfillAction<FileTypeT>,
        toInPlaceUpdate: PolyfilledSingleArtifact<FileTypeT, PluginTypeT>
    ) {
        getSingleArtifactContainer(toInPlaceUpdate).inPlaceUpdate(action)
    }

    override fun <FileTypeT : FileSystemLocation> use(
        action: PolyfillAction<List<FileTypeT>>,
        toInPlaceUpdate: PolyfilledMultipleArtifact<FileTypeT, PluginTypeT>
    ) {
        getMultipleArtifactContainer(toInPlaceUpdate).inPlaceUpdate(action)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <FileTypeT : FileSystemLocation> getSingleArtifactContainer(
        artifactType: PolyfilledSingleArtifact<FileTypeT, *>
    ): SingleArtifactContainer<FileTypeT> = singleArtifactStorage[artifactType] as SingleArtifactContainer<FileTypeT>

    @Suppress("UNCHECKED_CAST")
    private fun <FileTypeT : FileSystemLocation> getMultipleArtifactContainer(
        artifactType: PolyfilledMultipleArtifact<FileTypeT, *>
    ): MultipleArtifactContainer<FileTypeT> =
        multipleArtifactStorage[artifactType] as MultipleArtifactContainer<FileTypeT>

}

class ApplicationArtifactsRepository(p: Project, v: Variant) :
    DefaultArtifactsRepository<PolyfilledApplicationArtifact>(p, v)

class LibraryArtifactsRepository(p: Project, v: Variant) :
    DefaultArtifactsRepository<PolyfilledLibraryArtifact>(p, v)