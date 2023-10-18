package me.xx2bab.polyfill.jar

import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.internal.scope.getRegularFiles
import com.android.build.gradle.internal.tasks.MergeJavaResourceTask
import me.xx2bab.polyfill.PolyfillAction
import me.xx2bab.polyfill.getCapitalizedName
import me.xx2bab.polyfill.task.MultipleArtifactTaskExtendConfiguration
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.withType

/**
 * To retrieve all java resources for external projects
 * that will participate the resource merge process.
 */
class JavaResourceMergeOfExtProjectsPreHookConfiguration(
    project: Project,
    appVariant: ApplicationVariant,
    actionList: () -> List<PolyfillAction<List<RegularFile>>>
) : MultipleArtifactTaskExtendConfiguration<RegularFile>(project, appVariant, actionList) {

    override val data: Provider<List<RegularFile>> = project.objects.listProperty<RegularFile>() // A placeholder

    override fun orchestrate() {
        val variantCapitalizedName = variant.getCapitalizedName()
        project.afterEvaluate {
            val mergeTask = project.tasks.withType<MergeJavaResourceTask>().first {
                it.name.contains(variantCapitalizedName, true)
                        && it.name.contains("test", true).not()
            }

            // Setup data
            (data as ListProperty<RegularFile>).set(mergeTask.externalLibJavaRes
                .getRegularFiles(project.rootProject.layout.projectDirectory))

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