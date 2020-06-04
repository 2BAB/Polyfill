package me.xx2bab.polyfill.manifest.post

import com.google.common.annotations.VisibleForTesting
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.LittleEndianOutputStream
import java.io.File

class ManifestPostTweaker {

    private val manifestBlock = ManifestBlock()

    fun read(source: File) {
        if (source.exists() && source.isFile && source.name == "AndroidManifest.xml") {
            val inputStream = LittleEndianInputStream(source)
            manifestBlock.parse(inputStream, 0)
            return
        }
        throw IllegalArgumentException("The input file is illegal.")
    }

    fun write(dest: File, manifest: ManifestBlock = manifestBlock) {
        if (dest.exists()) {
            dest.delete()
        }
        dest.parentFile.mkdirs()
        dest.createNewFile()
        val outputStream = LittleEndianOutputStream(dest)
        outputStream.writeByte(manifest.toByteArray())
        outputStream.close()
    }

    @VisibleForTesting
    fun getManifestBlock(): ManifestBlock {
        return manifestBlock
    }

}