package me.xx2bab.polyfill.manifest.post

import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import java.io.File

class ManifestPostTweaker {

    private val manifest = Manifest()

    fun read(source: File) {
        if (source.exists() && source.isFile && source.name == "AndroidManifest.xml") {
            val inputStream = LittleEndianInputStream(source)
            manifest.parse(inputStream, 0)
            return
        }
        throw IllegalArgumentException("The input file is illegal.")
    }

}