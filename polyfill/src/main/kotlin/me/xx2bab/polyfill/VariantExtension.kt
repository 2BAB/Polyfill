package me.xx2bab.polyfill

import com.android.build.api.variant.Variant
import org.gradle.configurationcache.extensions.capitalized

fun Variant.getCapitalizedName() = name.capitalized()