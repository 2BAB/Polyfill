package me.xx2bab.polyfill.arsc.pack.type

import me.xx2bab.polyfill.arsc.base.*
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import java.nio.ByteBuffer

class ResValue: IParsable {

    var size: Short = INVALID_VALUE_SHORT
    var res0: Byte = INVALID_VALUE_BYTE
    var dataType: Byte = INVALID_VALUE_BYTE
    var data: Int = INVALID_VALUE_INT

    override fun parse(input: LittleEndianInputStream, start: Long) {
        size = input.readShort()
        res0 = input.readByte()
        dataType = input.readByte()
        data = input.readInt()
    }

    override fun toByteArray(): ByteArray {
        val sizeSize = SIZE_SHORT
        val res0Size = SIZE_BYTE
        val dataTypeSize = SIZE_BYTE
        val dataSize = SIZE_INT
        val bf = ByteBuffer.allocate(sizeSize + res0Size + dataTypeSize + dataSize)
        bf.takeLittleEndianOrder()
        bf.putShort(size)
        bf.put(res0)
        bf.put(dataType)
        bf.putInt(data)
        return bf.flipToArray()
    }


}