package me.xx2bab.polyfill.manifest

import com.google.common.io.Resources.getResource
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.manifest.bytes.parser.ManifestBlock
import me.xx2bab.polyfill.manifest.bytes.parser.ManifestBytesTweaker
import me.xx2bab.polyfill.manifest.bytes.parser.body.XMLBodyType
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Currently we are doing integration test only for "ScratchPaper" project's Manifest file in bytes.
 */
class ManifestInBytesTweakerIntegrationTest {

    @Before
    fun setup() {

    }

    @Test
    fun fullIntegrationTest() {
        val originManifestFile = File(getResource("AndroidManifest.xml").toURI())
        val input = LittleEndianInputStream(originManifestFile)
        val manifestPostTweaker = ManifestBytesTweaker()
        manifestPostTweaker.read(originManifestFile)
        val manifest = manifestPostTweaker.getManifestBlock()

        validateStringPoolBlock(input, manifest)
        validateResourceIdBlock(input, manifest)
        validateNamespaceXmlBody(input, manifest)
        validateTagXmlBody(input, manifest)
        validateFile(originManifestFile, manifestPostTweaker)
        validatePackageNameModification(originManifestFile, manifestPostTweaker)
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

    private fun validateNamespaceXmlBody(input: LittleEndianInputStream,
                                         manifest: ManifestBlock) {
        val namespaceList = manifest.bodyList.filter {
            it.header.chunkType == XMLBodyType.START_NAMESPACE
                    || it.header.chunkType == XMLBodyType.END_NAMESPACE
        }
        for (namespace in namespaceList) {
            val originByteArray = ByteArray(namespace.header.chunkSize)
            input.seek(namespace.header.start)
            input.read(originByteArray)
            val outputByteArray = namespace.toByteArray()
            assertArrayEquals(originByteArray, outputByteArray)
        }
    }

    private fun validateTagXmlBody(input: LittleEndianInputStream,
                                         manifest: ManifestBlock) {
        val tagList = manifest.bodyList.filter {
            it.header.chunkType == XMLBodyType.START_TAG
                    || it.header.chunkType == XMLBodyType.END_TAG
        }
        for (tag in tagList) {
            val originByteArray = ByteArray(tag.header.chunkSize)
            input.seek(tag.header.start)
            input.read(originByteArray)
            val outputByteArray = tag.toByteArray()
            assertArrayEquals(originByteArray, outputByteArray)
        }
    }

    private fun validateFile(originManifestFile: File,
                             manifestPostTweaker: ManifestBytesTweaker) {
        val generatedManifestFile = File(originManifestFile.parentFile,
                "${originManifestFile.nameWithoutExtension}-modified.arsc")
        manifestPostTweaker.write(generatedManifestFile)
        assertArrayEquals(Files.readAllBytes(Paths.get(originManifestFile.absolutePath)),
                Files.readAllBytes(Paths.get(generatedManifestFile.absolutePath)))
        generatedManifestFile.delete()
    }

    private fun validatePackageNameModification(originManifestFile: File,
                                                manifestPostTweaker: ManifestBytesTweaker) {
        val newPackageName = "me.xx2bab.polyfill.manifest.test.packagename"
        val generatedManifestFile = File(originManifestFile.parentFile,
                "${originManifestFile.nameWithoutExtension}-modified.arsc")
        manifestPostTweaker.updatePackageName(newPackageName)
        manifestPostTweaker.write(generatedManifestFile)

        val newTweaker = ManifestBytesTweaker()
        newTweaker.read(generatedManifestFile)
        val valueIndex = newTweaker.getAttrFromTagAttrs(
                newTweaker.getSpecifyStartTagBodyByName("manifest")!!, "package")!!
                .valueIndex
        val value = newTweaker.getManifestBlock().stringBlock.strings[valueIndex]
        assertEquals(newPackageName, value)
    }

}