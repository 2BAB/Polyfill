package me.xx2bab.polyfill.arsc.pack.config

import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_BYTE
import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_INT
import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_SHORT
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream

class ResValue: IParsable {

    var size: Short = INVALID_VALUE_SHORT
    var res0: Byte = INVALID_VALUE_BYTE
    var dataType: Byte = INVALID_VALUE_BYTE
    var data: Int = INVALID_VALUE_INT

    override fun parse(input: LittleEndianInputStream, start: Long) {
        size = input.readShort()
        res0 = input.readByte()
        dataType = input.readByte()
        data = input.readInt()
    }

}