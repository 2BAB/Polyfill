package me.xx2bab.polyfill.arsc.stringpool

import me.xx2bab.polyfill.arsc.base.Header
import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_INT
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

class StringPool : IParsable {

    // Common header
    lateinit var header: Header

    var stringCount: Int = 0
    var styleCount: Int = 0
    var flag: Int = INVALID_VALUE_INT
    var stringStartPosition: Int = INVALID_VALUE_INT
    var styleStartPosition: Int = INVALID_VALUE_INT

    // The header has a little bit padding before move to string offset array
//    var paddingSize: Int = 0

    lateinit var stringOffsets: IntArray
    lateinit var styleOffsets: IntArray
    lateinit var strings: Array<ByteBuffer>
    lateinit var styles: Array<ByteBuffer>


    @Throws(IOException::class)
    override fun parse(input: LittleEndianInputStream, start: Long) {
        input.seek(start)
        header = Header()
        header.parse(input, start)

        stringCount = input.readInt()
        styleCount = input.readInt()
        flag = input.readInt()
        stringStartPosition = input.readInt()
        styleStartPosition = input.readInt()

        // This block is populated at very beginning place, so Int is quite enough to store and long is safe to convert
//        paddingSize = header.start.toInt() + header.headSize - input.filePointer.toInt()
        input.seek(header.start + header.headSize)

        stringOffsets = if (stringCount > 0) IntArray(stringCount) { input.readInt() } else IntArray(0)
        styleOffsets = if (styleCount > 0) IntArray(styleCount) { input.readInt() } else IntArray(0)

        input.seek(header.start + stringStartPosition)

        strings = if (stringCount > 0) {
            Array(stringCount) { i ->
                val buffer = if (i < stringCount - 1) {
                    ByteArray(stringOffsets[i + 1] - stringOffsets[i])
                } else {
                    if (styleCount > 0) {
                        ByteArray(styleStartPosition - stringOffsets[i] - stringStartPosition)
                    } else {
                        ByteArray(header.chunkSize - stringStartPosition - stringOffsets[i])
                    }
                }
                input.read(buffer)
                println(String(buffer, StandardCharsets.UTF_8))
                ByteBuffer.allocate(buffer.size).apply {
                    order(ByteOrder.LITTLE_ENDIAN)
                    clear()
                    put(buffer)
                }
            }
        } else {
            emptyArray()
        }
        styles = if (styleCount > 0) {
            Array(styleCount) { i ->
                val buffer = if (i < styleCount - 1) {
                    ByteArray(styleOffsets[i + 1] - styleOffsets[i])
                } else {
                    ByteArray(header.chunkSize - styleStartPosition - styleOffsets[i])
                }
                input.read(buffer)
                println(String(buffer, StandardCharsets.UTF_8))
                ByteBuffer.allocate(buffer.size).apply {
                    order(ByteOrder.LITTLE_ENDIAN)
                    clear()
                    put(buffer)
                }
            }
        } else {
            emptyArray()
        }
    }


}