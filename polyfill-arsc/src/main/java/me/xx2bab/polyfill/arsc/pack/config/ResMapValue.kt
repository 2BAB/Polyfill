package me.xx2bab.polyfill.arsc.pack.config

import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_INT
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream

class ResMapValue: IParsable {

    var name: Int = INVALID_VALUE_INT
    lateinit var resValue: ResValue

    override fun parse(input: LittleEndianInputStream, start: Long) {
        name = input.readInt()
        resValue = ResValue()
        resValue.parse(input, start + 4)
    }

}