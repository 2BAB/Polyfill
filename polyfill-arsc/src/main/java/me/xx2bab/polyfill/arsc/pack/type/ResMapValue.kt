package me.xx2bab.polyfill.arsc.pack.type

import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_INT
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.base.SIZE_INT
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import java.nio.ByteBuffer

class ResMapValue: IParsable {

    var name: Int = INVALID_VALUE_INT
    lateinit var resValue: ResValue

    override fun parse(input: LittleEndianInputStream, start: Long) {
        name = input.readInt()
        resValue = ResValue()
        resValue.parse(input, start + 4)
    }

    override fun toByteArray(): ByteArray {
        val nameSize = SIZE_INT
        val resValueByteArray = resValue.toByteArray()
        val resValueSize = resValueByteArray.size
        val bf = ByteBuffer.allocate(nameSize + resValueSize)
        bf.takeLittleEndianOrder()
        bf.putInt(name)
        bf.put(resValueByteArray)
        return bf.flipToArray()
    }


}