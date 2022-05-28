package me.xx2bab.polyfill

import com.android.build.api.component.analytics.AnalyticsEnabledApplicationVariant
import com.android.build.api.component.analytics.AnalyticsEnabledLibraryVariant
import com.android.build.api.component.impl.ApkCreationConfigImpl
import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.LibraryVariant
import com.android.build.api.variant.Variant
import com.android.build.api.variant.impl.ApplicationVariantImpl
import com.android.build.api.variant.impl.LibraryVariantImpl
import com.android.build.gradle.internal.scope.GlobalScope
import com.android.sdklib.BuildToolInfo
import org.gradle.api.provider.Provider

class BuildToolInfoPatch(private val variant: Variant): BackportPatch<Provider<BuildToolInfo>>() {

    override fun apply(): Provider<BuildToolInfo> {
        val globalScope = when (variant) {
            is ApplicationVariant -> {
                variant.getGlobalScope()
            }
            is LibraryVariant -> {
                variant.getGlobalScope()
            }
            else -> {
                throw UnsupportedOperationException("Can not convert $this to either ApplicationVariantImpl or LibraryVariantImpl.")
            }
        }
        return globalScope.versionedSdkLoader.flatMap { it.buildToolInfoProvider }
    }

    private fun LibraryVariant.getLibraryVariantImpl(): LibraryVariantImpl {
        return when (this) {
            is LibraryVariantImpl -> {
                this
            }
            is AnalyticsEnabledLibraryVariant -> {
                this.delegate as LibraryVariantImpl
            }
            else -> {
                throw UnsupportedOperationException("Can not convert $this to LibraryVariantImpl.")
            }
        }
    }

    private fun LibraryVariant.getGlobalScope() = getLibraryVariantImpl().globalScope

    /**
     * [GlobalScope] contains some build services / data providers. It's a supplementary entry
     * to our hooks, because some of its scope has been replaced by other components. This is a
     * common object that can be seen in all plugins.
     *
     * @return [GlobalScope]
     */
    private fun ApplicationVariant.getGlobalScope() = getApkCreationConfigImpl().globalScope



    /**
     * Indirect implementation of [com.android.build.gradle.internal.component.ApkCreationConfig].
     * The [ApkCreationConfigImpl.config] provides internal Artifacts APIs that can be consumed
     * for more intermediate files.
     *
     * @return [ApkCreationConfigImpl]
     */
    private fun ApplicationVariant.getApkCreationConfigImpl() = getApplicationVariantImpl().delegate

    private fun ApplicationVariant.getApplicationVariantImpl(): ApplicationVariantImpl {
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



}