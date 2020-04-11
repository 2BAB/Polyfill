package me.xx2bab.polyfill.arsc.pack

import me.xx2bab.polyfill.arsc.base.Header
import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_BYTE
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream

class TypeSpec : IParsable {

    lateinit var header: Header // Pass the header instance from resTable before using it or calling parse(...)
    var typeId: Byte = INVALID_VALUE_BYTE
    private var reservedField0: Byte = 0 // So far can ignore it
    private var reservedField1: Short = 0 // So far can ignore it
    var specCount: Int = 0
    lateinit var specArray: IntArray

    override fun parse(input: LittleEndianInputStream, start: Long) {
        typeId = input.readByte()
        reservedField0 = input.readByte()
        reservedField1 = input.readShort()
        specCount = input.readInt()
        specArray = IntArray(specCount) { input.readInt() }
//        print(specCount)
    }

}