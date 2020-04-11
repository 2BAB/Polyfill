package me.xx2bab.polyfill.arsc.pack.config

import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_INT
import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_SHORT
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream

class ResEntry: IParsable {
    var size: Short = 0 // Header size that contains size, flag, stringPoolIndex only
    var flag: Short = INVALID_VALUE_SHORT // Either RES_TABLE_ENTRY_FLAG_COMPLEX or RES_TABLE_ENTRY_FLAG_PUBLIC
    var stringPoolIndex = INVALID_VALUE_INT // The resource name index of Global String Pool

    // When FLAG_COMPLEX is 0
    lateinit var resValue: ResValue

    // When FLAG_COMPLEX is 1
    var parent: Int = INVALID_VALUE_INT // The parent ResMapEntry
    var pairCount: Int = 0 // The pair amount
    val resMapValues = mutableListOf<ResMapValue>()

    override fun parse(input: LittleEndianInputStream, start: Long) {
        size = input.readShort()
        flag = input.readShort()
        stringPoolIndex = input.readInt()

        if (flag.toInt() == 0) {
            resValue = ResValue()
            resValue.parse(input, input.filePointer)
        } else {
            parent = input.readInt()
            pairCount = input.readInt()
            if (pairCount > 0) {
                for (i in 0 until pairCount) {
                    val mapValue = ResMapValue()
                    mapValue.parse(input, input.filePointer)
                    resMapValues.add(mapValue)
                }
            }
        }
    }

}