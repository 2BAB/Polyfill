package me.xx2bab.polyfill.arsc.pack

import me.xx2bab.polyfill.arsc.base.Header
import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_BYTE
import me.xx2bab.polyfill.arsc.base.NO_ENTRY_INDEX
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.pack.config.ResConfig
import me.xx2bab.polyfill.arsc.pack.config.ResEntry

class Type : IParsable {

    lateinit var header: Header // Pass the header instance from resTable before using it or calling parse(...)
    var typeId: Byte = INVALID_VALUE_BYTE
    private var reservedField0: Byte = 0 // So far can ignore it, and it's always 0
    private var reservedField1: Short = 0 // So far can ignore it, and it's always 0
    var entryCount: Int = 0
    var entryStart: Int = 0
    lateinit var config: ResConfig
    lateinit var entryOffsets: IntArray
    lateinit var entries: Array<ResEntry?>

    override fun parse(input: LittleEndianInputStream, start: Long) {
        // The header should passed from outside, the start value is
        typeId = input.readByte()
        reservedField0 = input.readByte()
        reservedField1 = input.readShort()
        entryCount = input.readInt()
        entryStart = input.readInt()

        config = ResConfig()
        config.parse(input, start)

        entryOffsets = IntArray(entryCount) { input.readInt() }
        input.seek(header.start + entryStart)
        entries = Array(entryCount){
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

}