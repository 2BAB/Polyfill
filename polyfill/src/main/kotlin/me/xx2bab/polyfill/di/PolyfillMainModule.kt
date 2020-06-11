package me.xx2bab.polyfill.di

import me.xx2bab.polyfill.arsc.base.ResTable
import me.xx2bab.polyfill.arsc.export.IResArscTweaker
import me.xx2bab.polyfill.manifest.post.IManifestPostTweaker
import me.xx2bab.polyfill.manifest.post.ManifestPostTweaker
import org.koin.dsl.module

val mainModule = module {

    // AGP


    // Arsc
    single { ResTable() as IResArscTweaker }

    // Manifest
    single { ManifestPostTweaker() as IManifestPostTweaker }
}