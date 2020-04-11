package me.xx2bab.polyfill.buildscript.release

import me.xx2bab.polyfill.buildscript.BuildConfig
import org.gradle.api.Project
import java.io.File

object ReleaseMetaData {

    lateinit var project: Project

    fun init(project: Project) {
        ReleaseMetaData.project = project
    }

    fun getVersion(): String {
        return BuildConfig.Versions.polyfillDevVersion
    }

    fun getBuildFiles(): List<File> {
        val pwd = project.rootProject.rootDir.absolutePath
        val res = mutableListOf<File>()
        val javaProjectNames = arrayOf(
                "polyfill",
                "polyfill-arsc"
        )
        for (name in javaProjectNames) {
            val jar = File(arrayOf(pwd, name, "build", "libs", name + "-" + getVersion() + ".jar")
                    .joinToString(File.separator))
            res.add(jar)
        }
        return res
    }
}
