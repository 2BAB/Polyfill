package me.xx2bab.polyfill.manifest.byte.body

import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import java.nio.ByteBuffer

/**
 * Haven't done the content parsing, will add when some libs require changing it.
 */
class TextXmlBody: XMLBody() {

    lateinit var content: ByteArray

    override fun parse(input: LittleEndianInputStream, start: Long) {
        content = ByteArray(header.chunkSize - header.size())
        input.read(content)
    }

    override fun toByteArray(): ByteArray {
        val newChunkSize = header.size() + content.size
        val bf = ByteBuffer.allocate(newChunkSize)
        bf.takeLittleEndianOrder()

        bf.putInt(header.chunkType)
        bf.putInt(newChunkSize)
        bf.put(content)

        return bf.flipToArray()
    }
}