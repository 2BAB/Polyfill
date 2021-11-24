package me.xx2bab.polyfill.manifest.source

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.tasks.ProcessApplicationManifest
import me.xx2bab.polyfill.matrix.base.ApplicationSelfManageableProvider
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
        val tasks = project.tasks.withType(ProcessApplicationManifest::class.java)
        val task = tasks.filter {
            it.name.contains(variant.name, true)
        }.first()
        manifests = task.mainManifest.map {
            task.getManifests().elements.get()
        }
    }

    override fun get(defaultValue: Provider<Set<FileSystemLocation>>?): Provider<Set<FileSystemLocation>>? {
        return manifests
    }

    override fun isPresent() = ::manifests.isInitialized

}