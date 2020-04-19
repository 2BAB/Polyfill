package me.xx2bab.polyfill.arsc.pack

import me.xx2bab.polyfill.arsc.base.Header
import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_BYTE
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.base.sizeOf
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream

abstract class AbsResType: IParsable {

    lateinit var header: Header // Pass the header instance from resTable before using it or calling parse(...)
    var typeId: Byte = INVALID_VALUE_BYTE
    protected var reservedField0: Byte = 0 // So far can ignore it
    protected var reservedField1: Short = 0 // So far can ignore it

    override fun parse(input: LittleEndianInputStream, start: Long) {
        // The header should passed from outside, the start value is
        typeId = input.readByte()
        reservedField0 = input.readByte()
        reservedField1 = input.readShort()
    }

    fun commonHeaderSize(): Int {
        return header.size() + sizeOf(typeId) + sizeOf(reservedField0) + sizeOf(reservedField1)
    }

}