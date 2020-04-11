package me.xx2bab.polyfill.arsc.base

import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import java.io.IOException

class Header: IParsable {

    var start: Long = 0
    var type: Short = INVALID_VALUE_SHORT
    var headSize: Short = 0
    var chunkSize: Int = 0

    @Throws(IOException::class)
    override fun parse(input: LittleEndianInputStream, start: Long) {
        input.seek(start)
        this.start = start
        type = input.readShort()
        headSize = input.readShort()
        chunkSize = input.readInt()
    }

    override fun toString(): String {
        return "Header(start=$start, type=$type, headSize=$headSize, chunkSize=$chunkSize)"
    }


}