package me.xx2bab.polyfill.manifest

import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.manifest.post.ManifestBlock
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test
import java.io.File

class ManifestPostTweakerIntegrationTest {

    @Before
    fun setup() {

    }

    @Test
    fun fullIntegrationTest() {
        val originManifestFile = File("/Users/2bab/Desktop/AndroidManifest.xml")
        val input = LittleEndianInputStream(originManifestFile)
        val manifest = ManifestBlock()
        manifest.parse(input, 0)
//        val manifestPostTweaker = ManifestPostTweaker()
//        manifestPostTweaker.read(originManifestFile)

        validateStringPoolBlock(input, manifest)
        validateResourceIdBlock(input, manifest)
        validateFile()
    }

    private fun validateStringPoolBlock(input: LittleEndianInputStream,
                                        manifest: ManifestBlock) {
        val originByteArray = ByteArray(manifest.stringBlock.header.chunkSize)
        input.seek(manifest.stringBlock.header.start)
        input.read(originByteArray)
        val outputByteArray = manifest.stringBlock.toByteArray()
        assertArrayEquals(originByteArray, outputByteArray)
    }

    private fun validateResourceIdBlock(input: LittleEndianInputStream,
                                        manifest: ManifestBlock) {
        val originByteArray = ByteArray(manifest.resourceIdBlock.header.chunkSize)
        input.seek(manifest.resourceIdBlock.header.start)
        input.read(originByteArray)
        val outputByteArray = manifest.resourceIdBlock.toByteArray()
        assertArrayEquals(originByteArray, outputByteArray)
    }

    private fun validateFile() {

    }

}