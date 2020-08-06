package me.xx2bab.polyfill.manifest.bytes.parser

import com.google.common.annotations.VisibleForTesting
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.LittleEndianOutputStream
import me.xx2bab.polyfill.manifest.bytes.parser.body.Attribute
import me.xx2bab.polyfill.manifest.bytes.parser.body.StartTagXmlBody
import me.xx2bab.polyfill.manifest.bytes.parser.body.XMLBodyType
import java.io.File

class ManifestBytesTweaker : IManifestBytesTweaker {

    private val manifestBlock = ManifestBlock()

    override fun read(source: File) {
        if (source.exists() && source.isFile /*&& source.name == "AndroidManifest.xml"*/) {
            val inputStream = LittleEndianInputStream(source)
            manifestBlock.parse(inputStream, 0)
            return
        }
        throw IllegalArgumentException("The input file is illegal.")
    }

    override fun write(dest: File) {
        write(dest, manifestBlock)
    }

    override fun write(dest: File, manifest: ManifestBlock) {
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

    override fun updatePackageName(newPackageName: String) {
        val applicationTag = getSpecifyStartTagBodyByName("manifest")
                ?: throw IllegalStateException("Could not found <manifest> tag")
        val ackageName = getAttrFromTagAttrs("package", applicationTag)
                ?: throw IllegalStateException("Could not found package")
        manifestBlock.stringBlock.strings[ackageName.valueIndex] = newPackageName
    }

    fun getSpecifyStartTagBodyByName(tagName: String): StartTagXmlBody? {
        val list = manifestBlock.bodyList
                .filter { it.header.chunkType == XMLBodyType.START_TAG }
                .filter { manifestBlock.stringBlock.strings[(it as StartTagXmlBody).name] == tagName }
        return if (list.isNullOrEmpty()) {
            null
        } else {
            list[0] as StartTagXmlBody
        }
    }

    fun getAttrFromTagAttrs(attrName: String, tag: StartTagXmlBody): Attribute? { // ignore the uri so far
        val res = tag.attrs.filter { manifestBlock.stringBlock.strings[it.nameIndex] == attrName }
        return if (res.isNullOrEmpty()) null else res[0]
    }

}