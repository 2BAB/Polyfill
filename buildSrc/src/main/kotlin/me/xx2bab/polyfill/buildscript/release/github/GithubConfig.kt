package me.xx2bab.polyfill.buildscript.release.github

import java.io.Serializable

interface GithubConfig : Serializable {

    fun getOwner(): String {
        return "2bab"
    }

    fun getToken(): String

    fun getRepo(): String

    fun getTagBranch(): String


}