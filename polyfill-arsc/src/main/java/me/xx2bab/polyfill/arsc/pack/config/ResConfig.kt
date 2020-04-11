package me.xx2bab.polyfill.arsc.pack.config

import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream

class ResConfig: IParsable {

    var configSize: Int = 0
    lateinit var config: ByteArray

    override fun parse(input: LittleEndianInputStream, start: Long) {
        configSize = input.readInt()
        if (configSize > 4) {
            // Deduct the Int size of size itself
            config = ByteArray(configSize - 4)
            input.read(config)
        }
    }

}