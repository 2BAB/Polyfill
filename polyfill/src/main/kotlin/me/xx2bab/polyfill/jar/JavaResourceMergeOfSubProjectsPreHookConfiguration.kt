package me.xx2bab.polyfill.jar

import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.internal.scope.getDirectories
import com.android.build.gradle.internal.tasks.MergeJavaResourceTask
import me.xx2bab.polyfill.PolyfillAction
import me.xx2bab.polyfill.getCapitalizedName
import me.xx2bab.polyfill.task.MultipleArtifactTaskExtendConfiguration
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.withType

/**
 * To retrieve all java resources for sub-projects (except current module)
 * that will participate the resource merge process.
 */
class JavaResourceMergeOfSubProjectsPreHookConfiguration(
    project: Project,
    appVariant: ApplicationVariant,
    actionList: () -> List<PolyfillAction<List<Directory>>>
) : MultipleArtifactTaskExtendConfiguration<Directory>(project, appVariant, actionList) {

    override val data: Provider<List<Directory>> = project.objects.listProperty<Directory>() // A placeholder

    override fun orchestrate() {
        val variantCapitalizedName = variant.getCapitalizedName()
        project.afterEvaluate {
            val mergeTask = project.tasks.withType<MergeJavaResourceTask>().first {
                it.name.contains(variantCapitalizedName, true)
                        && it.name.contains("test", true).not()
            }

            // Setup data }
            (data as ListProperty<Directory>).set(mergeTask.subProjectJavaRes
                .getDirectories(project.rootProject.layout.projectDirectory))

            val localData = data
            // Setup in-place-update
            actionList().forEachIndexed { index, action ->
                action.onTaskConfigure(mergeTask)
                mergeTask.doFirst("JavaResourceMergePreHookByPolyfill$index") {
                    action.onExecute(localData)
                }
            }
        }
    }


}