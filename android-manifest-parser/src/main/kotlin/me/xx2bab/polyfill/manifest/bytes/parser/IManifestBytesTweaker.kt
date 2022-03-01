package me.xx2bab.polyfill.manifest.bytes.parser

import java.io.File

interface IManifestBytesTweaker {

    fun read(source: File)

    fun write(dest: File)

    fun write(dest: File, manifest: ManifestBlock)

    fun updatePackageName(newPackageName: String)

}