package me.xx2bab.polyfill.gradle.provider

import org.gradle.api.Project


abstract class BaseGradleDataProvider<T>(private val project: Project) {

    abstract fun get(defaultValue: T? = null): T?

    abstract fun isPresent(): Boolean

}