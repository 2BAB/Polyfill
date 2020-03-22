package me.xx2bab.polyfill.buildscript.release.github

import com.github.breadmoirai.GithubReleaseTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import java.io.File

class GithubManipulator(val githubConfig: GithubConfig) {

    fun createGithubReleaseTask(project: Project,
                                artifacts: List<File>,
                                version: String,
                                releaseNotes: String): Task {
        val id = version.replace(".", "")
        return project.tasks.create<GithubReleaseTask>("release${id}ToGithub") {
            // Logger.i("Configurate Github Release Task")
            // e54c906d37bf216432d4cdf340116bbf4635dbe3
            setToken(githubConfig.getToken())
            setOwner(githubConfig.getOwner())
            setRepo(githubConfig.getRepo())
            setTagName(version)
            setTargetCommitish(githubConfig.getTagBranch())
            setReleaseName("v${version}")
            setBody(releaseNotes)
            setDraft(false)
            setPrerelease(false)
            setReleaseAssets(artifacts)
            setOverwrite(true)
        }
    }

}