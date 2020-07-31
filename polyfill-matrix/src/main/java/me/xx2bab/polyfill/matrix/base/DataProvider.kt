package me.xx2bab.polyfill.matrix.base

import org.gradle.api.Project

interface DataProvider<T> {

    fun initialize(project: Project)

    fun get(defaultValue: T? = null): T?

    fun isPresent(): Boolean

}