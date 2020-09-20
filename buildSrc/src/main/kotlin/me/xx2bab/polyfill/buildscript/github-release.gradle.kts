package me.xx2bab.polyfill.buildscript

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import me.xx2bab.polyfill.buildscript.BuildConfig.Path
import me.xx2bab.polyfill.buildscript.BuildConfig.Versions
import org.apache.commons.lang.StringUtils
import java.util.*

val taskName = "releaseArtifactsToGithub"
val artifacts: DirectoryProperty = project.objects.directoryProperty()
artifacts.set(Path.getAggregatedJarDirectory(project))

// Temporary workaround for directory is not recognized by ReleaseAssets
gradle.taskGraph.whenReady {
    beforeTask {
        if (this is GithubReleaseTask) {
            this.setReleaseAssets(Path.getAggregatedJarDirectory(project).listFiles())
        }
    }
}

val token: String = if (StringUtils.isNotBlank(System.getenv("GH_DEV_TOKEN"))) {
    System.getenv("GH_DEV_TOKEN")
} else {
    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())
    properties.getProperty("github.devtoken")
}

val repo = "polyfill"
val tagBranch = "master"
val version = Versions.polyfillDevVersion
val releaseNotes = ""
createGithubReleaseTaskInternal(artifacts, token, repo, tagBranch, version, releaseNotes)


fun createGithubReleaseTaskInternal(artifacts: DirectoryProperty,
                                    token: String,
                                    repo: String,
                                    tagBranch: String,
                                    version: String,
                                    releaseNotes: String): TaskProvider<GithubReleaseTask> {
//    val id = version.replace(".", "")
    return project.tasks.register<GithubReleaseTask>("releaseArtifactsToGithub") {
        setAuthorization("Token $token")
        setOwner("2bab")
        setRepo(repo)
        setTagName(version)
        setTargetCommitish(tagBranch)
        setReleaseName("v${version}")
        setBody(releaseNotes)
        setDraft(false)
        setPrerelease(false)
        setReleaseAssets(artifacts)
        setOverwrite(true)
        setAllowUploadToExisting(true)
        setApiEndpoint("https://api.github.com")
        setDryRun(false)
    }
}

