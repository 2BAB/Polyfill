package me.xx2bab.polyfill.manifest.source

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.VariantProperties
import com.android.build.gradle.tasks.ProcessApplicationManifest
import me.xx2bab.polyfill.matrix.base.ApplicationSelfManageableProvider
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileSystemLocation

class ManifestMergeInputProvider: ApplicationSelfManageableProvider<Set<FileSystemLocation>> {

    private lateinit var manifests: FileCollection

    override fun initialize(project: Project,
                            androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                            variant: VariantProperties) {
        val t = project.tasks.withType(ProcessApplicationManifest::class.java)
        manifests = t.first().getManifests()
    }

    override fun get(defaultValue: Set<FileSystemLocation>?): Set<FileSystemLocation>? {
        return manifests.elements.get()
    }

    override fun isPresent() = ::manifests.isInitialized && manifests.elements.isPresent

}