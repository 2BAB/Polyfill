package me.xx2bab.polyfill.res

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.DependencyResourcesComputer
import com.android.build.gradle.tasks.MergeResources
import me.xx2bab.polyfill.agp.toTaskContainer
import me.xx2bab.polyfill.base.ApplicationSelfManageableProvider
import me.xx2bab.polyfill.tools.ReflectionKit
import org.gradle.api.Project
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import java.io.File

class ResourcesMergeInputProvider : ApplicationSelfManageableProvider<Provider<Set<FileSystemLocation>>> {

    private lateinit var resources: Provider<Set<FileSystemLocation>>

    override fun initialize(
        project: Project,
        androidExtension: AndroidComponentsExtension<*, *, *>,
        variant: Variant
    ) {
        // MergeResources#getConfiguredResourceSets(...)
        // It may consume eagerly if you don't put it inside the tasks.register("..."){ ... } block.
        val mergeTask = variant.toTaskContainer().mergeResourcesTask.get()
        val resourcesComputer = ReflectionKit.getField(
            MergeResources::class.java,
            mergeTask,
            "resourcesComputer"
        ) as DependencyResourcesComputer
        val resourceSets = resourcesComputer.compute(mergeTask.processResources, null)
        val resourceFiles = resourceSets.mapNotNull { resourceSet ->
            val getSourceFiles = resourceSet.javaClass.methods.find {
                it.name == "getSourceFiles" && it.parameterCount == 0
            }
            @Suppress("UNCHECKED_CAST")
            getSourceFiles?.invoke(resourceSet) as? Iterable<File>
        }.flatten()
        resources = project.files(resourceFiles).elements
    }

    override fun obtain(defaultValue: Provider<Set<FileSystemLocation>>?): Provider<Set<FileSystemLocation>> {
        return resources
    }

}