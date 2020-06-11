package me.xx2bab.polyfill.manifest.post

import java.io.File

interface IManifestPostTweaker {

    fun read(source: File)

    fun write(dest: File)

    fun write(dest: File, manifest: ManifestBlock)

    fun updatePackageName(newPackageName: String)

}