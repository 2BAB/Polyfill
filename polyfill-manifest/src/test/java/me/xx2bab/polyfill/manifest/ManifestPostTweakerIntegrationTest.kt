package me.xx2bab.polyfill.manifest

import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.manifest.post.ManifestPostTweaker
import org.junit.Before
import org.junit.Test
import java.io.File

class ManifestPostTweakerIntegrationTest {

    @Before
    fun setup() {

    }

    @Test
    fun validateFile() {
        val originManifestFile = File("/Users/2bab/Desktop/AndroidManifest.xml")
        val input = LittleEndianInputStream(originManifestFile)
        val manifestPostTweaker = ManifestPostTweaker()
        manifestPostTweaker.read(originManifestFile)
    }

}