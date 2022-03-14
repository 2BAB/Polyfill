package me.xx2bab.polyfill.manifest.bytes.parser.body

import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_INT
import me.xx2bab.polyfill.arsc.base.sizeOf
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import java.nio.ByteBuffer

open class StartTagXmlBody: XMLBody() {

    var namespaceUri = INVALID_VALUE_INT
    var name = INVALID_VALUE_INT
    var reservedField1 = 0x140014
    var attributeCount = INVALID_VALUE_INT
    var classAttribute = INVALID_VALUE_INT
    val attrs = mutableListOf<Attribute>()

    override fun parse(input: LittleEndianInputStream, start: Long) {
        super.parse(input, start)

        namespaceUri = input.readInt()
        name = input.readInt()
        reservedField1 = input.readInt()
        attributeCount = input.readInt()
        classAttribute = input.readInt()
        for (i in 0 until attributeCount) {
            val attr = Attribute()
            attr.parse(input, input.filePointer)
            attrs.add(attr)
        }
    }

    override fun toByteArray(): ByteArray {
        val newAttributeCount = attrs.size
        val attrsByteArray = attrs.map { it.toByteArray() }
        val attrsLength = attrsByteArray.sumBy { it.size }
        val newChunkSize = (header.size()
                + sizeOf(lineNumber)
                + sizeOf(reservedField0)
                + sizeOf(namespaceUri)
                + sizeOf(name)
                + sizeOf(reservedField1)
                + sizeOf(attributeCount)
                + sizeOf(classAttribute)
                + attrsLength)
        val bf = ByteBuffer.allocate(newChunkSize)
        bf.takeLittleEndianOrder()

        bf.putInt(header.chunkType)
        bf.putInt(newChunkSize)
        bf.putInt(lineNumber)
        bf.putInt(reservedField0)
        bf.putInt(namespaceUri)
        bf.putInt(name)
        bf.putInt(reservedField1)
        bf.putInt(newAttributeCount)
        bf.putInt(classAttribute)
        attrsByteArray.forEach { bf.put(it) }

        return bf.flipToArray()
    }



}