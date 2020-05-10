package me.xx2bab.polyfill.manifest.post

import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_INT
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.base.sizeOf
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream

class Header: IParsable {

    var start: Long = 0
    var chunkType: Int = INVALID_VALUE_INT
    var chunkSize: Int = 0

    override fun parse(input: LittleEndianInputStream, start: Long) {
        this.start = start
        chunkType = input.readInt()
        chunkSize = input.readInt()
    }

    override fun toByteArray(): ByteArray {
        return ByteArray(0)
    }

    fun size(): Int {
        return sizeOf(chunkType) + sizeOf(chunkSize)
    }


}