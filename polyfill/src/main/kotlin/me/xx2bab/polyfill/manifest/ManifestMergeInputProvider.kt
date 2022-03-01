package me.xx2bab.polyfill.manifest

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import me.xx2bab.polyfill.agp.toApkCreationConfigImpl
import me.xx2bab.polyfill.base.ApplicationSelfManageableProvider
import org.gradle.api.Project
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider

class ManifestMergeInputProvider : ApplicationSelfManageableProvider<Provider<Set<FileSystemLocation>>> {

    private lateinit var manifests: Provider<Set<FileSystemLocation>>

    override fun initialize(
        project: Project,
        androidExtension: AndroidComponentsExtension<*, *, *>,
        variant: Variant
    ) {
        // ProcessApplicationManifest#configure(...)
        manifests = variant.toApkCreationConfigImpl()
            .config
            .variantDependencies
            .getArtifactCollection(
                AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                AndroidArtifacts.ArtifactScope.ALL,
                AndroidArtifacts.ArtifactType.MANIFEST
            )
            .artifactFiles
            .elements
    }

    override fun obtain(defaultValue: Provider<Set<FileSystemLocation>>?): Provider<Set<FileSystemLocation>> {
        return manifests
    }

}