package me.xx2bab.polyfill.buildscript.release

import me.xx2bab.polyfill.buildscript.release.github.GithubConfig
import me.xx2bab.polyfill.buildscript.release.github.GithubManipulator
import me.xx2bab.polyfill.buildscript.release.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class DeployPlugin : Plugin<Project> {

    companion object {
        const val RELEASE_TASK_NAME: String = "deployRelease"
    }

    override fun apply(project: Project) {
        val isDebugMode = project.hasProperty("debugReleasePlugin")
        if (!isDebugMode && !project.gradle.startParameter.taskNames.contains(RELEASE_TASK_NAME)) {
            return
        }
        Logger.init(project)

        project.afterEvaluate {
            ReleaseMetaData.init(project)

            if (isDebugMode) {
                Logger.i("version:" + ReleaseMetaData.getVersion())
            }

            project.tasks.create<ReleaseTask>(RELEASE_TASK_NAME) {

                ///////////////// Github Release
                val githubConfig = object : GithubConfig {

                    override fun getRepo(): String {
                        return "Polyfill"
                    }

                    override fun getTagBranch(): String {
                        return "master"
                    }

                    override fun getToken(): String {
                        return System.getenv("GH_DEV_TOKEN")
                    }

                }
                val githubManipulator = GithubManipulator(githubConfig)
                val githubReleaseFiles = ReleaseMetaData.getBuildFiles()

                val githubReleaseTask = githubManipulator.createGithubReleaseTask(project,
                        githubReleaseFiles,
                        ReleaseMetaData.getVersion(),
                        "Release Note: to be filled in...")

                this.dependsOn(githubReleaseTask)
            }
        }
    }


}