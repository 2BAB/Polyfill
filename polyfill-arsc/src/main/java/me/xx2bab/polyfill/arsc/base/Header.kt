package me.xx2bab.polyfill.arsc.base

import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * The common header for per chunk.
 */
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

    /**
     * To generate a header by ByteArray, you should create it manually by
     *
     * - Passing current start index;
     * - Passing the type from the original Header;
     * - Passing the headSize from the original Header (So far the size is fixed per chunk);
     * - Passing the chunkSize calculating from chunk's #toByteArray().
     *
     * DO NOT CALL THIS METHOD IN ANY CASES.
     */
    override fun toByteArray(): ByteArray {
        return ByteArray(0)
    }

    fun size(): Int {
        return sizeOf(type) + sizeOf(headSize) + sizeOf(chunkSize)
    }

}