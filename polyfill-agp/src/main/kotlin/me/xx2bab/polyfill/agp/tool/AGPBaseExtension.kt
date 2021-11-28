package me.xx2bab.polyfill.agp.tool

import com.android.build.api.variant.Variant
import com.android.build.api.variant.impl.ApplicationVariantImpl

fun Variant.toApplicationVariantImpl() = (this as ApplicationVariantImpl)

fun Variant.toApkCreationConfigImpl() = (this as ApplicationVariantImpl).delegate