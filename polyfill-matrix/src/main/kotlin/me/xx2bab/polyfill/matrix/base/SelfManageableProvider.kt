package me.xx2bab.polyfill.matrix.base

import com.android.build.api.extension.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import org.gradle.api.Project

interface SelfManageableProvider<T> {

    fun initialize(project: Project,
                   androidExtension: AndroidComponentsExtension<*, *>,
                   variant: Variant)

    fun get(defaultValue: T? = null): T?

    fun isPresent(): Boolean

}