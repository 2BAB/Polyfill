package me.xx2bab.polyfill.agp.provider

import me.xx2bab.polyfill.gradle.provider.BaseGradleDataProvider
import me.xx2bab.polyfill.gradle.tool.SemanticVersionLite
import org.gradle.api.Project


class AGPVersionProvider(private val project: Project)
    : BaseGradleDataProvider<SemanticVersionLite>(project) {

    override fun get(defaultValue: SemanticVersionLite?): SemanticVersionLite? {
        return null
    }

    override fun isPresent(): Boolean {
        return false
    }


}