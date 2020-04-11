package me.xx2bab.polyfill.arsc.pack

import me.xx2bab.polyfill.arsc.base.Header
import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_INT
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.base.RES_TABLE_TYPE_SPEC_TYPE
import me.xx2bab.polyfill.arsc.base.RES_TABLE_TYPE_TYPE
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.stringpool.StringPool
import java.io.IOException

class ResPackage: IParsable {

    lateinit var header: Header
    var packageId: Int = 0
    val packageName: ByteArray = ByteArray(256) // Can convert to a string
    var resTypeStringPoolOffset: Int = INVALID_VALUE_INT
    var lastPublicType: Int = INVALID_VALUE_INT
    var resKeywordStringPoolOffset: Int = INVALID_VALUE_INT
    var lastPublicKey: Int = INVALID_VALUE_INT

    lateinit var resTypeStringPool: StringPool
    lateinit var resKeywordStringPool: StringPool

    @Throws(IOException::class)
    override fun parse(input: LittleEndianInputStream, start: Long) {
        input.seek(start)

        // the header size counts:
        //
        // packageId,
        // packageName,
        // resTypeStringPoolOffset,
        // lastPublicType,
        // resKeywordStringPoolOffset,
        // lastPublicKey
        header = Header()
        header.parse(input, start)
        packageId = input.readInt()
        input.read(packageName)
        resTypeStringPoolOffset = input.readInt()
        lastPublicType = input.readInt()
        resKeywordStringPoolOffset = input.readInt()
        lastPublicKey = input.readInt()

        resTypeStringPool = StringPool()
        resTypeStringPool.parse(input, header.start + resTypeStringPoolOffset)
        resKeywordStringPool = StringPool()
        resKeywordStringPool.parse(input, header.start + resKeywordStringPoolOffset)

        while (input.filePointer < header.start + header.chunkSize) {
            val typeHeader = Header()
            val currStart = input.filePointer
            typeHeader.parse(input, currStart)
            if (typeHeader.type == RES_TABLE_TYPE_SPEC_TYPE) {
                val typeSpec = TypeSpec()
                typeSpec.header = typeHeader
                typeSpec.parse(input, currStart)
            } else if (typeHeader.type == RES_TABLE_TYPE_TYPE) {
                val configList = Type()
                configList.header = typeHeader
                configList.parse(input, currStart)
            }
            input.seek(typeHeader.start + typeHeader.chunkSize)
        }
    }




}