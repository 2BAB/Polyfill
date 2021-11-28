package me.xx2bab.polyfill.agp.tool

import com.android.build.api.variant.Variant
import com.android.build.api.variant.impl.ApplicationVariantImpl

fun Variant.toApplicationVariantImpl() = (this as ApplicationVariantImpl)

fun Variant.toApkCreationConfigImpl() = toApplicationVariantImpl().delegate

fun Variant.toGlobalScope() = toApkCreationConfigImpl().globalScope

fun Variant.toTaskContainer() = toApkCreationConfigImpl().config.taskContainer