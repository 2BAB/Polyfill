import me.xx2bab.polyfill.buildscript.BuildConfig.Path
import me.xx2bab.polyfill.buildscript.BuildConfig.Versions

plugins {
    id("me.xx2bab.polyfill.buildscript.github-release")
}

allprojects {
    version = Versions.polyfillDevVersion
    group = "me.2bab"
}

task("clean") {
    delete(rootProject.buildDir)
}

val aggregateJars by tasks.registering {
    doLast {
        val output = Path.getAggregatedJarDirectory(project)
        output.mkdir()
        subprojects {
            File(buildDir.absolutePath + File.separator + "libs").walk()
                    .filter { it.name.startsWith(this.name) && it.extension == "jar" }
                    .forEach { it.copyTo(File(output, it.name)) }
        }
    }
}
