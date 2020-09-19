package me.xx2bab.polyfill.buildscript

import org.apache.commons.lang.StringUtils
import java.util.*

plugins{
    `maven-publish`
    id("com.jfrog.bintray")
}

val groupName = "me.2bab"
val projectName = "polyfill"
val mavenDesc = "Hook Toolset for Android App Build System."
val baseUrl = "https://github.com/2BAB/Polyfill"
val siteUrl = baseUrl
val gitUrl = "$baseUrl.git"
val issueUrl = "$baseUrl/issues"

val licenseIds = "Apache-2.0"
val licenseNames = arrayOf("The Apache Software License, Version 2.0")
val licenseUrls = arrayOf("http://www.apache.org/licenses/LICENSE-2.0.txt")
val inception = "2018"

val username = "2BAB"


publishing {
    publications {
        create<MavenPublication>("PolyfillArtifact") {
            from(components["java"])
            pom {
                // Description
                name.set(projectName)
                description.set(mavenDesc)
                url.set(siteUrl)

                // Archive
                groupId = groupName
                artifactId = project.name
                version = BuildConfig.Versions.polyfillDevVersion

                // License
                inceptionYear.set(inception)
                licenses {
                    licenseNames.forEachIndexed { ln, li ->
                        license {
                            name.set(li)
                            url.set(licenseUrls[ln])
                        }
                    }
                }
                developers {
                    developer {
                        name.set(username)
                    }
                }
                scm {
                    connection.set(gitUrl)
                    developerConnection.set(gitUrl)
                    url.set(siteUrl)
                }
            }
        }
    }

    repositories {
        maven {
            name = "myMavenlocal"
            url = uri(System.getProperty("user.home") + "/.m2/repository")
        }
    }
}

var btUser: String?
var btApiKey: String?

if (StringUtils.isNotBlank(System.getenv("BINTRAY_USER"))) {
    btUser = System.getenv("BINTRAY_USER")
    btApiKey = System.getenv("BINTRAY_APIKEY")
} else {
    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())
    btUser = properties.getProperty("bintray.user")
    btApiKey = properties.getProperty("bintray.apikey")
}

bintray{
    user = btUser
    key = btApiKey
    setPublications("PolyfillArtifact")
    pkg.apply {
        repo = "maven"
        name = project.name
        desc = mavenDesc
        websiteUrl = siteUrl
        issueTrackerUrl = issueUrl
        vcsUrl = gitUrl
        setLabels("2BAB", "Polyfill", "Gradle", "Android Gradle Plugin", "AGP", "Hook")
        setLicenses(licenseIds)
        publish = true
        publicDownloadNumbers = true
    }
}