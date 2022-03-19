package me.xx2bab.polyfill.manifest.bytes.parser.body

import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_INT
import me.xx2bab.polyfill.arsc.base.sizeOf
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import java.nio.ByteBuffer

class EndTagXmlBody: XMLBody() {

    var prefix = INVALID_VALUE_INT
    var uri = INVALID_VALUE_INT

    override fun parse(input: LittleEndianInputStream, start: Long) {
        super.parse(input, start)
        prefix = input.readInt()
        uri = input.readInt()
    }

    override fun toByteArray(): ByteArray {
        val newChunkSize = (header.size()
                + sizeOf(lineNumber)
                + sizeOf(reservedField0)
                + sizeOf(prefix)
                + sizeOf(uri))
        val bf = ByteBuffer.allocate(newChunkSize)
        bf.takeLittleEndianOrder()

        bf.putInt(header.chunkType)
        bf.putInt(newChunkSize)
        bf.putInt(lineNumber)
        bf.putInt(reservedField0)
        bf.putInt(prefix)
        bf.putInt(uri)

        return bf.flipToArray()
    }
}