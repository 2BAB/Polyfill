package me.xx2bab.polyfill.manifest.post

import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_INT
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.base.SIZE_INT
import me.xx2bab.polyfill.arsc.base.sizeOf
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import me.xx2bab.polyfill.arsc.stringpool.UtfUtil
import java.nio.ByteBuffer

class StringPoolBlock: IParsable {

    lateinit var header: Header
    var stringCount: Int = 0
    var styleCount: Int = 0
    var reservedField0: Int = INVALID_VALUE_INT
    var stringStartPosition: Int = INVALID_VALUE_INT
    var styleStartPosition: Int = INVALID_VALUE_INT

    lateinit var stringOffsets: IntArray
    lateinit var styleOffsets: IntArray
    lateinit var stringByteArrays: Array<ByteArray>
    lateinit var stylesByteArrays: Array<ByteArray>
    lateinit var strings: Array<String?>
    lateinit var styles: Array<String?>

    override fun parse(input: LittleEndianInputStream, start: Long) {
        input.seek(start)

        header = Header()
        header.parse(input, start)
        stringCount = input.readInt()
        styleCount = input.readInt()
        reservedField0 = input.readInt()
        stringStartPosition = input.readInt()
        styleStartPosition = input.readInt()

        stringOffsets = if (stringCount > 0) IntArray(stringCount) { input.readInt() } else IntArray(0)
        styleOffsets = if (styleCount > 0) IntArray(styleCount) { input.readInt() } else IntArray(0)

        input.seek(start + stringStartPosition)

        strings = Array(stringCount) { null }
        stringByteArrays = if (stringCount > 0) {
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
                strings[i] = if (array.isEmpty()) null else UtfUtil.byteArrayToString(array, -1)
                array
            }
        } else {
            emptyArray()
        }
        styles = Array(styleCount) { null }
        stylesByteArrays = if (styleCount > 0) {
            Array(styleCount) { i ->
                val array = if (i < styleCount - 1) {
                    ByteArray(styleOffsets[i + 1] - styleOffsets[i])
                } else {
                    ByteArray(header.chunkSize - styleStartPosition - styleOffsets[i])
                }
                input.read(array)
                styles[i] = if (array.isEmpty()) null else UtfUtil.byteArrayToString(array, -1)
                array
            }
        } else {
            emptyArray()
        }
    }

    override fun toByteArray(): ByteArray {
        val chunkTypeSize = sizeOf(header.chunkType)
        val chunkSizeSize = sizeOf(header.chunkSize)
        val stringCountSize = sizeOf(stringCount)
        val styleCountSize = sizeOf(styleCount)
        val reservedFieldSize = sizeOf(reservedField0)
        val stringStartPositionSize = sizeOf(stringStartPosition)
        val styleStartPositionSize = sizeOf(styleStartPosition)

        val newStringByteArrays = Array(strings.size) {
            val s = strings[it]
            if (s == null) ByteArray(0) else UtfUtil.stringToByteArray(s, -1)
        }
        val stringsSize = newStringByteArrays.sumBy { it.size }
        val stringsByteAlignedSupplementCount = 4 - stringsSize % 4
        val stringOffsetsSize = newStringByteArrays.size * SIZE_INT

        val newStyleByteArrays = Array(styles.size) {
            val s = styles[it]
            if (s == null) ByteArray(0) else UtfUtil.stringToByteArray(s, -1)
        }
        val stylesSize = newStyleByteArrays.sumBy { it.size }
        val stylesByteAlignedSupplementCount = 4 - stylesSize % 4
        val styleOffsetsSize = newStyleByteArrays.size * SIZE_INT

        val newStringOffsets = calculateOffsets(newStringByteArrays)
        val newStyleOffsets = calculateOffsets(newStyleByteArrays)

        val newChunkSize = (chunkTypeSize
                + chunkSizeSize
                + stringCountSize
                + styleCountSize
                + reservedFieldSize
                + stringStartPositionSize
                + styleStartPositionSize
                + stringsSize
                + stringsByteAlignedSupplementCount % 4
                + stringOffsetsSize
                + stylesSize
                + stylesByteAlignedSupplementCount % 4
                + styleOffsetsSize)

        val newStringStartPosition = newChunkSize - stylesSize - stringsSize - stringsByteAlignedSupplementCount % 4
        val newStyleStartPosition = if (stylesSize == 0) 0 else newChunkSize - stylesSize - stylesByteAlignedSupplementCount % 4


        val bf = ByteBuffer.allocate(newChunkSize)
        bf.takeLittleEndianOrder()

        bf.putInt(header.chunkType)
        bf.putInt(newChunkSize)
        bf.putInt(newStringByteArrays.size)
        bf.putInt(newStyleByteArrays.size)
        bf.putInt(reservedField0)
        bf.putInt(newStringStartPosition)
        bf.putInt(newStyleStartPosition)
        newStringOffsets.forEach { bf.putInt(it) }
        newStyleOffsets.forEach { bf.putInt(it) }
        newStringByteArrays.forEach { bf.put(it) }
        val zeroInByte: Byte = 0
        if (stringsByteAlignedSupplementCount != 4) {
            for (i in 0 until stringsByteAlignedSupplementCount) {
                bf.put(zeroInByte)
            }
        }
        newStyleByteArrays.forEach { bf.put(it) }
        if (stylesByteAlignedSupplementCount != 4) {
            for (i in 0 until stylesByteAlignedSupplementCount) {
                bf.put(zeroInByte)
            }
        }

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