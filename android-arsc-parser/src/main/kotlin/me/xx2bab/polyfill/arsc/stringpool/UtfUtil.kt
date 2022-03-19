package me.xx2bab.polyfill.arsc.stringpool

import com.google.common.io.ByteStreams
import com.google.common.primitives.UnsignedBytes
import me.xx2bab.polyfill.arsc.base.UTF8_FLAG
import java.io.DataOutput
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Convert from ArscBlamer repository.
 *
 * https://github.com/google/android-arscblamer/blob/master/java/com/google/devrel/gmscore/tools/apk/arsc/UtfUtil.java
 *
 * Created by Google.
 */
object UtfUtil {

    fun byteArrayToString(array: ByteArray, flag: Int): String {
        val buffer = ByteBuffer.wrap(array)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        var offset = 0
        val charCount = decodeLength(buffer, offset, flag)
        offset += computeLengthOffset(charCount, flag)
        return if (flag == UTF8_FLAG) {
            val length = decodeLength(buffer, offset, flag)
            offset += computeLengthOffset(length, flag)
            val originPosition = buffer.position()
            buffer.position(offset)
            try {
                String(decodeUtf8OrModifiedUtf8(buffer, charCount))
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

    private fun decodeUtf8OrModifiedUtf8(utf8Buffer: ByteBuffer, characterCount: Int): CharArray {
        val charBuffer = CharArray(characterCount)
        var offset = 0
        while (offset < characterCount) {
            offset = decodeUtf8OrModifiedUtf8CodePoint(utf8Buffer, charBuffer, offset)
        }
        return charBuffer
    }

    // This is a Javafied version of the implementation in ART:
    // cs/android/art/libdexfile/dex/utf-inl.h?l=32&rcl=4da82e1e9f201cb0e408499ee3b38cbca575698e
    private fun decodeUtf8OrModifiedUtf8CodePoint(`in`: ByteBuffer, out: CharArray, offset: Int): Int {
        var offset = offset
        val one = `in`.get().toInt()
        if (one and 0x80 == 0) {
            out[offset++] = one.toChar()
            return offset
        }
        val two = `in`.get().toInt()
        if (one and 0x20 == 0) {
            out[offset++] = (one and 0x1f shl 6 or (two and 0x3f)).toChar()
            return offset
        }
        val three = `in`.get().toInt()
        if (one and 0x10 == 0) {
            out[offset++] = (one and 0x0f shl 12 or (two and 0x3f shl 6) or (three and 0x3f)).toChar()
            return offset
        }
        val four = `in`.get().toInt()
        val codePoint: Int = one and 0x0f shl 18 or (two and 0x3f shl 12) or (three and 0x3f shl 6) or (four.toInt() and 0x3f)

        // Write the code point out as a surrogate pair
        out[offset++] = ((codePoint shr 10) + 0xd7c0 and 0xffff).toChar()
        out[offset++] = ((codePoint and 0x03ff) + 0xdc00).toChar()
        return offset
    }

}