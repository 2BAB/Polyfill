package me.xx2bab.polyfill.jar

import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.internal.scope.getRegularFiles
import com.android.build.gradle.internal.tasks.MergeJavaResourceTask
import me.xx2bab.polyfill.getCapitalizedName
import me.xx2bab.polyfill.getTaskContainer
import me.xx2bab.polyfill.task.MultipleArtifactPincerTaskConfiguration
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.withType

/**
 * To retrieve all java resources (except current module)
 * that will participate the merge process.
 */
class JavaResourceMergePreHookConfigureAction(
    project: Project,
    private val appVariant: ApplicationVariant,
    headTaskProvider: TaskProvider<*>,
    lazyLastTaskProvider: () -> TaskProvider<*>
) : MultipleArtifactPincerTaskConfiguration<RegularFile>(project, appVariant, headTaskProvider, lazyLastTaskProvider) {

    override val data: Provider<List<RegularFile>> = project.objects.listProperty<RegularFile>() // A placeholder

    override fun orchestrate() {
        val variantCapitalizedName = variant.getCapitalizedName()
        project.afterEvaluate {
            val mergeTask = project.tasks.withType<MergeJavaResourceTask>().first {
                it.name.contains(variantCapitalizedName)
                        && !it.name.contains("test", true)
            }

            // Setup Data
            val subProjectsJavaResList = mergeTask.subProjectJavaRes
                ?.getRegularFiles(project.rootProject.layout.projectDirectory)
                ?: project.objects.listProperty()
            val externalDepJavaResList = mergeTask.externalLibJavaRes
                ?.getRegularFiles(project.rootProject.layout.projectDirectory)
                ?: project.objects.listProperty()
            subProjectsJavaResList.zip(externalDepJavaResList) { a, b -> a + b }
            (data as ListProperty<RegularFile>).set(subProjectsJavaResList)


            // Setup dependencies
            // Left flank
            headTaskProvider.configure {
                dependsOn(appVariant.getTaskContainer().preBuildTask) // For current module
                // Initially it should use the Provider as the dependency directly,
                // however some dependencies were lost during the transformation from `FileCollection` to `Provider`.
                // dependsOn(data)
                dependsOn(mergeTask.subProjectJavaRes)
                dependsOn(mergeTask.externalLibJavaRes)
            }

            // Right flank
            mergeTask.dependsOn(lazyTailTaskProvider())
        }

    }


}