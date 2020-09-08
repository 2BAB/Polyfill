package me.xx2bab.polyfill.manifest.source

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.VariantProperties
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.tasks.ProcessApplicationManifest
import me.xx2bab.polyfill.matrix.base.SelfManageableProvider
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileSystemLocation

class ManifestMergeInputProvider: SelfManageableProvider<Set<FileSystemLocation>> {

    private lateinit var manifests: FileCollection

    override fun initialize(project: Project,
                            androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                            variantProperties: VariantProperties,
                            variantClassicProperties: BaseVariant) {
        variantClassicProperties.outputs.single {
            val processAppManifestTask = it.processManifestProvider.get() as ProcessApplicationManifest
            manifests = processAppManifestTask.getManifests()
            true
        }
    }

    override fun get(defaultValue: Set<FileSystemLocation>?): Set<FileSystemLocation>? {
        return manifests.elements.get()
    }

    override fun isPresent() = ::manifests.isInitialized && manifests.elements.isPresent

}