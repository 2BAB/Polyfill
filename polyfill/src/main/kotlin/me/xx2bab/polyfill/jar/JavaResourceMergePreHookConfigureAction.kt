package me.xx2bab.polyfill.jar

import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.internal.scope.getRegularFiles
import com.android.build.gradle.internal.tasks.MergeJavaResourceTask
import com.android.build.gradle.internal.tasks.ProcessJavaResTask
import me.xx2bab.polyfill.getCapitalizedName
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
        project.afterEvaluate {
            val variantCapitalizedName = variant.getCapitalizedName()
            val mergeTask = project.tasks.withType<MergeJavaResourceTask>().first {
                it.name.contains(variantCapitalizedName)
                        && !it.name.contains("test", true)
            }


            // Setup Data
//            val subProjectsJavaResList = appVariant.getApplicationVariantImpl().variantDependencies
//                .getArtifactFileCollection(
//                    AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
//                    AndroidArtifacts.ArtifactScope.PROJECT,
//                    AndroidArtifacts.ArtifactType.JAVA_RES
//                )
//                .getRegularFiles(project.rootProject.layout.projectDirectory)
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
            project.rootProject.subprojects {
                val subProject = this
                if (subProject !== project) {
                    subProject.tasks.whenTaskAdded {
                        val targetTask = this
                        if (targetTask is ProcessJavaResTask
                            && targetTask.name.contains(variantCapitalizedName)
                            && !targetTask.name.contains("test", true)
                        ) {
                            headTaskProvider.configure {
                                dependsOn(targetTask)
                            }
                        }
                    }
                }
            }

            // Right flank
            mergeTask.dependsOn(lazyTailTaskProvider())
        }
    }


}