package me.xx2bab.polyfill

import com.android.build.gradle.internal.DependencyResourcesComputer
import com.android.build.gradle.tasks.MergeResources
import me.xx2bab.polyfill.tools.ReflectionKit
import org.gradle.api.Project
import org.gradle.api.file.Directory
import java.io.File

class ResourceMergePreHookPatch(private val mergeTask: MergeResources,
                                private val project: Project): BackportPatch<List<Directory>>() {

    override fun apply(): List<Directory> {
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
        return resourceFiles.map { file ->
            // A hacky way to transform File -> RegularFile
            val rp = project.objects.directoryProperty()
            rp.fileValue(file)
            rp.get()
        }
    }

}