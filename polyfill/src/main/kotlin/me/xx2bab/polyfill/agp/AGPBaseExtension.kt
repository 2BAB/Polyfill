package me.xx2bab.polyfill.agp

import com.android.build.api.component.analytics.AnalyticsEnabledApplicationVariant
import com.android.build.api.variant.Variant
import com.android.build.api.variant.impl.ApplicationVariantImpl

fun Variant.toApplicationVariantImpl() : ApplicationVariantImpl {
    return when (this) {
        is ApplicationVariantImpl -> {
            this
        }
        is AnalyticsEnabledApplicationVariant -> {
            this.delegate as ApplicationVariantImpl
        }
        else -> {
            throw UnsupportedOperationException("Can not convert $this to ApplicationVariantImpl.")
        }
    }
}

fun Variant.toApkCreationConfigImpl() = toApplicationVariantImpl().delegate

fun Variant.toGlobalScope() = toApkCreationConfigImpl().globalScope

fun Variant.toTaskContainer() = toApkCreationConfigImpl().config.taskContainer