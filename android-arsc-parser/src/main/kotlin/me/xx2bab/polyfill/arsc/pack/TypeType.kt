package me.xx2bab.polyfill.arsc.pack

import me.xx2bab.polyfill.arsc.base.NO_ENTRY_INDEX
import me.xx2bab.polyfill.arsc.base.SIZE_INT
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import me.xx2bab.polyfill.arsc.pack.type.ResConfig
import me.xx2bab.polyfill.arsc.pack.type.ResEntry
import java.nio.ByteBuffer

class TypeType : AbsResType() {

    var entryCount: Int = 0
    var entryStart: Int = 0
    lateinit var config: ResConfig
    lateinit var entryOffsets: IntArray
    lateinit var entries: Array<ResEntry?>

    override fun parse(input: LittleEndianInputStream, start: Long) {
        // The header should passed from outside, the start value is
        super.parse(input, start)
        entryCount = input.readInt()
        entryStart = input.readInt()

        config = ResConfig()
        config.parse(input, input.filePointer)

        entryOffsets = IntArray(entryCount) { input.readInt() }
        input.seek(header.start + entryStart)
        entries = Array(entryCount) {
            if (entryOffsets[it] != NO_ENTRY_INDEX.toInt()) {
                input.seek(header.start + entryStart + entryOffsets[it])
                val entry = ResEntry()
                entry.parse(input, input.filePointer)
                entry
            } else {
                null
            }
        }
    }

    override fun toByteArray(): ByteArray {
        val commonHeaderSize = commonHeaderSize()

        val configByteArray = config.toByteArray()
        val configSize = configByteArray.size
        val newEntryCount = entries.size
        val entryCountSize = SIZE_INT
        val newEntryByteArray: List<ByteArray?> = entries.map { it?.toByteArray() }
        val entrySize = newEntryByteArray.sumBy { it?.size ?: 0 }

        val newEntryOffsets = IntArray(entryCount)
        var currentPointer = 0
        var lastSize = 0
        for (i in 0 until newEntryCount) {
            val eba = newEntryByteArray[i]
            if (i == 0) {
                if (eba == null) {
                    newEntryOffsets[i] = NO_ENTRY_INDEX.toInt()
                } else {
                    newEntryOffsets[i] = 0
                    lastSize = eba.size
                }
            } else {
                if (eba == null) {
                    newEntryOffsets[i] = NO_ENTRY_INDEX.toInt()
                } else {
                    currentPointer += lastSize
                    lastSize = eba.size
                    newEntryOffsets[i] = currentPointer
                }
            }
        }
        val entryOffsetsSize = entries.size * SIZE_INT
        val entryStartSize = SIZE_INT

        val newChunkSize = (commonHeaderSize
                + entryCountSize
                + entryStartSize
                + configSize
                + entryOffsetsSize
                + entrySize)
        val newEntryStart = newChunkSize - entrySize


        val bf = ByteBuffer.allocate(newChunkSize)
        bf.takeLittleEndianOrder()

        bf.putShort(header.type)
        bf.putShort((newChunkSize - entryOffsetsSize - entrySize).toShort())
        bf.putInt(newChunkSize)

        bf.put(typeId)
        bf.put(reservedField0)
        bf.putShort(reservedField1)

        bf.putInt(newEntryCount)
        bf.putInt(newEntryStart)

        bf.put(configByteArray)

        newEntryOffsets.forEach { bf.putInt(it) }

        newEntryByteArray.forEach { it?.let { bf.put(it) } }

        return bf.flipToArray()
    }


}