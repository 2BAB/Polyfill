package me.xx2bab.polyfill.manifest.post.body

import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_INT
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.base.sizeOf
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import java.nio.ByteBuffer

class Attribute: IParsable {

    var namespaceUriAttr = INVALID_VALUE_INT // -1 means null
    var nameAttr = INVALID_VALUE_INT // -1 means null
    var valueStr = INVALID_VALUE_INT // -1 means null
    var type = INVALID_VALUE_INT // >> 24
    var data = INVALID_VALUE_INT

    override fun parse(input: LittleEndianInputStream, start: Long) {
        namespaceUriAttr = input.readInt()
        nameAttr = input.readInt()
        valueStr = input.readInt()
        type = input.readInt()
        data = input.readInt()
    }

    override fun toByteArray(): ByteArray {
        val newChunkSize = (sizeOf(namespaceUriAttr)
                + sizeOf(nameAttr)
                + sizeOf(valueStr)
                + sizeOf(type)
                + sizeOf(data))
        val bf = ByteBuffer.allocate(newChunkSize)
        bf.takeLittleEndianOrder()

        bf.putInt(namespaceUriAttr)
        bf.putInt(nameAttr)
        bf.putInt(valueStr)
        bf.putInt(type)
        bf.putInt(data)

        return bf.flipToArray()
    }

}