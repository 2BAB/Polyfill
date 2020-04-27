package me.xx2bab.polyfill.arsc.io

import java.nio.ByteBuffer

/**
 * Convert from ArscBlamer repository.
 *
 * https://github.com/google/android-arscblamer/blob/master/java/com/google/devrel/gmscore/tools/apk/arsc/UtfUtil.java
 *
 * Created by Google.
 */
object UtfUtil {

    fun decodeUtf8OrModifiedUtf8(utf8Buffer: ByteBuffer, characterCount: Int): CharArray {
        val charBuffer = CharArray(characterCount)
        var offset = 0
        while (offset < characterCount) {
            offset = decodeUtf8OrModifiedUtf8CodePoint(utf8Buffer, charBuffer, offset)
        }
        return charBuffer
    }

    // This is a Javafied version of the implementation in ART:
    // cs/android/art/libdexfile/dex/utf-inl.h?l=32&rcl=4da82e1e9f201cb0e408499ee3b38cbca575698e
    fun decodeUtf8OrModifiedUtf8CodePoint(`in`: ByteBuffer, out: CharArray, offset: Int): Int {
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