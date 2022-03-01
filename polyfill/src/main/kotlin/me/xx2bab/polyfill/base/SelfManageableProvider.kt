package me.xx2bab.polyfill.base

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import org.gradle.api.Project

interface SelfManageableProvider<T> {

    fun initialize(project: Project,
                   androidExtension: AndroidComponentsExtension<*, *, *>,
                   variant: Variant)

    fun obtain(defaultValue: T? = null): T

}