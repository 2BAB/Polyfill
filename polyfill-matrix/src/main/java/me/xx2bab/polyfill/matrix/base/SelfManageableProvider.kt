package me.xx2bab.polyfill.matrix.base

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.VariantProperties
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project

interface SelfManageableProvider<T> {

    fun initialize(project: Project,
                   androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                   variantProperties: VariantProperties,
                   variantClassicProperties: BaseVariant)

    fun get(defaultValue: T? = null): T?

    fun isPresent(): Boolean

}