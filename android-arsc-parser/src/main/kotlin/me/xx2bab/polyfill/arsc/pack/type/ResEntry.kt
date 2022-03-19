package me.xx2bab.polyfill.arsc.pack.type

import me.xx2bab.polyfill.arsc.base.*
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import java.nio.ByteBuffer

class ResEntry : IParsable {
    var start: Long = 0
    var size: Short = 0 // Header size that contains size, flag, stringPoolIndex only
    var flag: Short = INVALID_VALUE_SHORT // Either RES_TABLE_ENTRY_FLAG_COMPLEX or RES_TABLE_ENTRY_FLAG_PUBLIC
    var stringPoolIndex = INVALID_VALUE_INT // The resource name index of Global String Pool

    // When FLAG_COMPLEX is 0
    lateinit var resValue: ResValue

    // When FLAG_COMPLEX is 1
    var parent: Int = INVALID_VALUE_INT // The parent ResMapEntry
    var pairCount: Int = 0 // The pair amount
    val resMapValues = mutableListOf<ResMapValue>()

    override fun parse(input: LittleEndianInputStream, start: Long) {
        this.start = start
        size = input.readShort()
        flag = input.readShort()
        stringPoolIndex = input.readInt()

        if (flag.toInt() == 0) {
            resValue = ResValue()
            resValue.parse(input, input.filePointer)
        } else {
            parent = input.readInt()
            pairCount = input.readInt()
            if (pairCount > 0) {
                for (i in 0 until pairCount) {
                    val mapValue = ResMapValue()
                    mapValue.parse(input, input.filePointer)
                    resMapValues.add(mapValue)
                }
            }
        }
    }

    override fun toByteArray(): ByteArray {
        val sizeSize = SIZE_SHORT
        val flagSize = SIZE_SHORT
        val stringPoolIndexSize = SIZE_INT
        val contentByteArray = if (flag.toInt() == 0) {
            resValue.toByteArray()
        } else {
            val resMapByteArrays = resMapValues.map { it.toByteArray() }
            val resMapSize = resMapByteArrays.sumBy { it.size }
            val parentSize = SIZE_INT
            val pairCount = SIZE_INT
            val mapChunkBuffer = ByteBuffer.allocate(parentSize + pairCount + resMapSize)
            mapChunkBuffer.takeLittleEndianOrder()
            mapChunkBuffer.putInt(parent)
            mapChunkBuffer.putInt(resMapByteArrays.size)
            resMapByteArrays.forEach { mapChunkBuffer.put(it) }
            mapChunkBuffer.flipToArray()
        }

        val newChunkSize = (sizeSize
                + flagSize
                + stringPoolIndexSize
                + contentByteArray.size)
        val bf = ByteBuffer.allocate(newChunkSize)
        bf.takeLittleEndianOrder()
        bf.putShort(size)
        bf.putShort(flag)
        bf.putInt(stringPoolIndex)
        bf.put(contentByteArray)
        return bf.flipToArray()
    }


}