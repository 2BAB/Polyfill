package me.xx2bab.polyfill.arsc.stringpool

import com.google.common.io.ByteStreams
import com.google.common.primitives.UnsignedBytes
import me.xx2bab.polyfill.arsc.base.*
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.UtfUtil
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import java.io.DataOutput
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder.LITTLE_ENDIAN

class StringPool : IParsable {

    companion object {

        fun byteArrayToString(array: ByteArray, flag: Int): String {
            val buffer = ByteBuffer.wrap(array)
            buffer.order(LITTLE_ENDIAN)
            var offset = 0
            val charCount = decodeLength(buffer, offset, flag)
            offset += computeLengthOffset(charCount, flag)
            return if (flag == UTF8_FLAG) {
                val length = decodeLength(buffer, offset, flag)
                offset += computeLengthOffset(length, flag)
                val originPosition = buffer.position()
                buffer.position(offset)
                try {
                    String(UtfUtil.decodeUtf8OrModifiedUtf8(buffer, charCount))
                } finally {
                    buffer.position(originPosition)
                }
            } else {
                String(buffer.array(), offset, charCount * 2, Charsets.UTF_16LE)
            }
        }

        fun stringToByteArray(str: String, flag: Int): ByteArray {
            val bytes: ByteArray = str.toByteArray(if (flag == UTF8_FLAG) Charsets.UTF_8 else Charsets.UTF_16LE)
            val dataOutput = ByteStreams.newDataOutput(bytes.size + 5);
            encodeLength(dataOutput, str.length, flag)
            if (flag == UTF8_FLAG) {
                encodeLength(dataOutput, bytes.size, flag)
            }
            dataOutput.write(bytes)
            if (flag == UTF8_FLAG) {
                dataOutput.write(0)
            } else {
                dataOutput.writeShort(0)
            }
            return dataOutput.toByteArray()
        }

        private fun encodeLength(output: DataOutput, length: Int, flag: Int) {
            if (length < 0) {
                output.write(0)
                return
            }
            if (flag == UTF8_FLAG) {
                if (length > 0x7F) {
                    output.write(length and 0x7F00 shr 8 or 0x80)
                }
                output.write(length and 0xFF)
            } else {  // UTF-16
                // TODO(acornwall): Replace output with a little-endian output.
                if (length > 0x7FFF) {
                    val highBytes = length and 0x7FFF0000 shr 16 or 0x8000
                    output.write(highBytes and 0xFF)
                    output.write(highBytes and 0xFF00 shr 8)
                }
                val lowBytes = length and 0xFFFF
                output.write(lowBytes and 0xFF)
                output.write(lowBytes and 0xFF00 shr 8)
            }
        }

        private fun decodeLength(buffer: ByteBuffer, offset: Int, flag: Int): Int {
            return if (flag == UTF8_FLAG) {
                decodeLengthUTF8(buffer, offset)
            } else {
                decodeLengthUTF16(buffer, offset)
            }
        }

        private fun decodeLengthUTF8(buffer: ByteBuffer, offset: Int): Int {
            // UTF-8 strings use a clever variant of the 7-bit integer for packing the string length.
            // If the first byte is >= 0x80, then a second byte follows. For these values, the length
            // is WORD-length in big-endian & 0x7FFF.
            var length = UnsignedBytes.toInt(buffer[offset])
            if (length and 0x80 != 0) {
                length = length and 0x7F shl 8 or UnsignedBytes.toInt(buffer[offset + 1])
            }
            return length
        }

        private fun decodeLengthUTF16(buffer: ByteBuffer, offset: Int): Int {
            // UTF-16 strings use a clever variant of the 7-bit integer for packing the string length.
            // If the first word is >= 0x8000, then a second word follows. For these values, the length
            // is DWORD-length in big-endian & 0x7FFFFFFF.
            var length: Int = buffer.getShort(offset).toInt() and 0xFFFF
            if (length and 0x8000 != 0) {
                length = ((length and 0x7FFF) shl 16) or (buffer.getShort(offset + 2).toInt() and 0xFFFF)
            }
            return length
        }

        private fun computeLengthOffset(length: Int, flag: Int): Int {
            return (if (flag == UTF8_FLAG) 1 else 2) * (if (length >= (if (flag == UTF8_FLAG) 0x80 else 0x8000)) 2 else 1)
        }
    }

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
    lateinit var strings: Array<ByteArray>
    lateinit var styles: Array<ByteArray>


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
                val array = if (i < stringCount - 1) {
                    ByteArray(stringOffsets[i + 1] - stringOffsets[i])
                } else {
                    if (styleCount > 0) {
                        ByteArray(styleStartPosition - stringOffsets[i] - stringStartPosition)
                    } else {
                        ByteArray(header.chunkSize - stringStartPosition - stringOffsets[i])
                    }
                }
                input.read(array)
                array
            }
        } else {
            emptyArray()
        }
        styles = if (styleCount > 0) {
            Array(styleCount) { i ->
                val array = if (i < styleCount - 1) {
                    ByteArray(styleOffsets[i + 1] - styleOffsets[i])
                } else {
                    ByteArray(header.chunkSize - styleStartPosition - styleOffsets[i])
                }
                input.read(array)
                array
            }
        } else {
            emptyArray()
        }
    }

    override fun toByteArray(): ByteArray {
        val headerSize = header.size()
        val stringCountSize = sizeOf(stringCount)
        val styleCountSize = sizeOf(styleCount)
        val flagSize = sizeOf(flag)
        val stringStartPositionSize = sizeOf(stringStartPosition)
        val styleStartPositionSize = sizeOf(styleStartPosition)

        val stringsSize = strings.sumBy { it.size }
        val stringOffsetsSize = strings.size * SIZE_INT
        val stylesSize = styles.sumBy { it.size }
        val styleOffsetsSize = styles.size * SIZE_INT

        val newStringOffsets = calculateOffsets(strings)
        val newStyleOffsets = calculateOffsets(styles)

        val newChunkSize = (headerSize
                + stringCountSize
                + styleCountSize
                + flagSize
                + stringStartPositionSize
                + styleStartPositionSize
                + stringsSize
                + stringOffsetsSize
                + stylesSize
                + styleOffsetsSize)

        val newStringStartPosition = newChunkSize - stylesSize - stringsSize
        val newStyleStartPosition = if (stylesSize == 0) 0 else newChunkSize - stylesSize


        val bf = ByteBuffer.allocate(newChunkSize)
        bf.takeLittleEndianOrder()
        bf.putShort(header.type)
        bf.putShort((headerSize
                + stringCountSize
                + styleCountSize
                + flagSize
                + stringStartPositionSize
                + styleStartPositionSize).toShort())
        bf.putInt(newChunkSize)
        bf.putInt(strings.size)
        bf.putInt(styles.size)
        bf.putInt(flag)
        bf.putInt(newStringStartPosition)
        bf.putInt(newStyleStartPosition)
        newStringOffsets.forEach { bf.putInt(it) }
        newStyleOffsets.forEach { bf.putInt(it) }
        strings.forEach { bf.put(it) }
        styles.forEach { bf.put(it) }

        return bf.flipToArray()
    }

    private fun calculateOffsets(array: Array<ByteArray>): IntArray {
        val offsets = IntArray(array.size)
        var currentPointer = 0
        var lastSize = 0
        for (i in array.indices) {
            val s = array[i]
            if (i == 0) {
                offsets[i] = 0
                lastSize = s.size
            } else {
                currentPointer += lastSize
                lastSize = s.size
                offsets[i] = currentPointer
            }
        }
        return offsets
    }

}