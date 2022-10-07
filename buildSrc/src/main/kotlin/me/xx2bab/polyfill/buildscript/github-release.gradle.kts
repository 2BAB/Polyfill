package me.xx2bab.polyfill.buildscript

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import me.xx2bab.polyfill.buildscript.BuildConfig.Path
import me.xx2bab.polyfill.buildscript.BuildConfig.Versions
import java.util.*

val taskName = "releaseArtifactsToGithub"

val tokenFromEnv: String? = System.getenv("GH_DEV_TOKEN")
val token: String = if (!tokenFromEnv.isNullOrBlank()) {
    tokenFromEnv
} else if (project.rootProject.file("local.properties").exists()){
    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())
    properties.getProperty("github.devtoken")
} else {
    ""
}

val repo = "polyfill"
val tagBranch = "master"
val version = Versions.polyfillDevVersion
val releaseNotes = ""
createGithubReleaseTaskInternal(token, repo, tagBranch, version, releaseNotes)


fun createGithubReleaseTaskInternal(
    token: String,
    repo: String,
    tagBranch: String,
    version: String,
    releaseNotes: String
): TaskProvider<GithubReleaseTask> {
//    val id = version.replace(".", "")
    return project.tasks.register<GithubReleaseTask>("releaseArtifactsToGithub") {
        authorization.set("Token $token")
        owner.set("2bab")
        this.repo.set(repo)
        tagName.set(version)
        targetCommitish.set(tagBranch)
        releaseName.set("v${version}")
        body.set(releaseNotes)
        draft.set(false)
        prerelease.set(false)
        overwrite.set(true)
        allowUploadToExisting.set(true)
        apiEndpoint.set("https://api.github.com")
        dryRun.set(false)
        generateReleaseNotes.set(false)
        releaseAssets.from(fileTree(Path.getAggregatedJarDirectory(project)))
    }
}

