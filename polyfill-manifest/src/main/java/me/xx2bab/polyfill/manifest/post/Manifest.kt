package me.xx2bab.polyfill.manifest.post

import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_INT
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream

class Manifest: IParsable {

    var magicNumber: Int = INVALID_VALUE_INT // It's a fixed number 0x80003
    var fileSize: Int = INVALID_VALUE_INT

    override fun parse(input: LittleEndianInputStream, start: Long) {
        magicNumber = input.readInt()
        fileSize = input.readInt()
    }

    override fun toByteArray(): ByteArray {
        return ByteArray(0)
    }


}