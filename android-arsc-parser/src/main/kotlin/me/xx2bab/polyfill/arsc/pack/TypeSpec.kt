package me.xx2bab.polyfill.arsc.pack

import me.xx2bab.polyfill.arsc.base.SIZE_INT
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import java.nio.ByteBuffer

class TypeSpec : AbsResType() {

    var specCount: Int = 0
    lateinit var specArray: IntArray

    override fun parse(input: LittleEndianInputStream, start: Long) {
        super.parse(input, start)
        specCount = input.readInt()
        specArray = IntArray(specCount) { input.readInt() }
    }

    override fun toByteArray(): ByteArray {
        val commonHeaderSize = commonHeaderSize()
        val specCountSize = SIZE_INT
        val specArraySize = SIZE_INT * specArray.size
        val newChunkSize = commonHeaderSize + specCountSize + specArraySize

        val bf = ByteBuffer.allocate(newChunkSize)
        bf.takeLittleEndianOrder()
        bf.putShort(header.type)
        bf.putShort((commonHeaderSize + specCountSize).toShort())
        bf.putInt(newChunkSize)
        bf.put(typeId)
        bf.put(reservedField0)
        bf.putShort(reservedField1)
        bf.putInt(specArray.size)
        specArray.forEach { bf.putInt(it) }

        return bf.flipToArray()
    }

}