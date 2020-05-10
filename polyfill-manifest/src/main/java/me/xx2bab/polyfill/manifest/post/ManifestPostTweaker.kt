package me.xx2bab.polyfill.manifest.post

import com.google.common.annotations.VisibleForTesting
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import java.io.File

class ManifestPostTweaker {

    private val manifest = ManifestBlock()

    fun read(source: File) {
        if (source.exists() && source.isFile && source.name == "AndroidManifest.xml") {
            val inputStream = LittleEndianInputStream(source)
            manifest.parse(inputStream, 0)
            return
        }
        throw IllegalArgumentException("The input file is illegal.")
    }

    @VisibleForTesting
    private fun getManifestBlock(): ManifestBlock {
        return manifest
    }

}