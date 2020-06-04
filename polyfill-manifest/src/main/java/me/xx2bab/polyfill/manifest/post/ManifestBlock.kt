package me.xx2bab.polyfill.manifest.post

import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_INT
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.base.sizeOf
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import me.xx2bab.polyfill.manifest.post.body.*
import java.nio.ByteBuffer

class ManifestBlock : IParsable {

    var magicNumber: Int = INVALID_VALUE_INT // It's a fixed number 0x80003
    var fileSize: Int = INVALID_VALUE_INT
    lateinit var stringBlock: StringPoolBlock
    lateinit var resourceIdBlock: ResourceIdBlock
    val bodyList = mutableListOf<XMLBody>()

    override fun parse(input: LittleEndianInputStream, start: Long) {
        magicNumber = input.readInt()
        fileSize = input.readInt()

        stringBlock = StringPoolBlock()
        stringBlock.parse(input, input.filePointer)

        resourceIdBlock = ResourceIdBlock()
        resourceIdBlock.parse(input, input.filePointer)

        while (input.filePointer < fileSize) {
            val bodyHeader = Header()
            bodyHeader.parse(input, input.filePointer)
            val xmlBody = when (bodyHeader.chunkType) {
                XMLBodyType.START_NAMESPACE -> StartNamespaceXmlBody()
                XMLBodyType.END_NAMESPACE -> EndNamespaceXmlBody()
                XMLBodyType.START_TAG -> StartTagXmlBody()
                XMLBodyType.END_TAG -> EndTagXmlBody()
                XMLBodyType.TEXT -> TextXmlBody()
                else -> throw Exception("Unsupported XMLBodyType: ${bodyHeader.chunkType}")
            }
            xmlBody.header = bodyHeader
            xmlBody.parse(input, input.filePointer)
            bodyList.add(xmlBody)

            input.seek(bodyHeader.start)
            input.skip(bodyHeader.chunkSize.toLong())
        }
    }

    override fun toByteArray(): ByteArray {
        val stringBlockByteArray = stringBlock.toByteArray()
        val resourceIdBlockByteArray = resourceIdBlock.toByteArray()
        val bodyListByteArrayList = bodyList.map { it.toByteArray() }
        val bodyListByteArrayListSize = bodyListByteArrayList.sumBy { it.size }
        val newChunkSize = (sizeOf(magicNumber)
                + sizeOf(fileSize)
                + stringBlockByteArray.size
                + resourceIdBlockByteArray.size
                + bodyListByteArrayListSize)
        val bf = ByteBuffer.allocate(newChunkSize)
        bf.takeLittleEndianOrder()

        bf.putInt(magicNumber)
        bf.putInt(newChunkSize)
        bf.put(stringBlockByteArray)
        bf.put(resourceIdBlockByteArray)
        bodyListByteArrayList.forEach { bf.put(it) }

        return bf.flipToArray()
    }


}