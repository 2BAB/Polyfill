package me.xx2bab.polyfill.manifest.bytes.parser.body

import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_INT
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.manifest.bytes.parser.Header

abstract class XMLBody: IParsable {

    lateinit var header: Header // Passed from outside
    var lineNumber: Int = 0
    var reservedField0 = INVALID_VALUE_INT

    override fun parse(input: LittleEndianInputStream, start: Long) {
        lineNumber = input.readInt()
        reservedField0 = input.readInt()
    }
}