package me.xx2bab.polyfill.arsc.pack

import me.xx2bab.polyfill.arsc.base.*
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import me.xx2bab.polyfill.arsc.stringpool.StringPool
import java.io.IOException
import java.nio.ByteBuffer

class ResPackage : IParsable {

    lateinit var header: Header
    var packageId: Int = 0
    val packageName: ByteArray = ByteArray(256) // Can convert to a string
    var resTypeStringPoolOffset: Int = INVALID_VALUE_INT
    var lastPublicType: Int = INVALID_VALUE_INT
    var resKeywordStringPoolOffset: Int = INVALID_VALUE_INT
    var lastPublicKey: Int = INVALID_VALUE_INT
    var typeIdOffset: Int = INVALID_VALUE_INT

    lateinit var resTypeStringPool: StringPool
    lateinit var resKeywordStringPool: StringPool
    val resTypes = mutableListOf<AbsResType>()

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
        typeIdOffset = input.readInt()

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
                resTypes.add(typeSpec)
            } else if (typeHeader.type == RES_TABLE_TYPE_TYPE) {
                val typeTypeConfigList = TypeType()
                typeTypeConfigList.header = typeHeader
                typeTypeConfigList.parse(input, currStart)
                resTypes.add(typeTypeConfigList)
            }
            input.seek(typeHeader.start + typeHeader.chunkSize)
        }
    }

    override fun toByteArray(): ByteArray {
        val headSize = header.size()
        val packageIdSize = sizeOf(packageId)
        val packageNameSize = packageName.size
        val resTypeStringPoolOffsetSize = sizeOf(resKeywordStringPoolOffset)
        val lastPublicTypeSize = sizeOf(lastPublicType)
        val resKeywordStringPoolOffsetSize = sizeOf(resKeywordStringPoolOffset)
        val lastPublicKeySize = sizeOf(lastPublicKey)
        val typeIdOffsetSize = sizeOf(typeIdOffset)

        val resTypeStringPoolByteArray = resTypeStringPool.toByteArray()
        val resTypeStringPoolByteArraySize = resTypeStringPoolByteArray.size
        val resKeywordStringPoolByteArray = resKeywordStringPool.toByteArray()
        val resKeywordStringPoolByteArraySize = resKeywordStringPoolByteArray.size

        val resTypesByteArrays = resTypes.map { it.toByteArray() }
        val resTypesSize = resTypesByteArrays.sumBy { it.size }

        val newChunkSize = (headSize
                + packageIdSize
                + packageNameSize
                + resTypeStringPoolOffsetSize
                + lastPublicTypeSize
                + resKeywordStringPoolOffsetSize
                + lastPublicKeySize
                + typeIdOffsetSize
                + resTypeStringPoolByteArraySize
                + resKeywordStringPoolByteArraySize
                + resTypesSize)
        val headerSize = (newChunkSize - resTypeStringPoolByteArraySize
                - resKeywordStringPoolByteArraySize - resTypesSize)

        val newResTypeStringPoolOffset = headerSize
        val newResKeywordStringPoolOffset = if (resKeywordStringPoolByteArraySize == 0)  {
            0
        } else {
            newResTypeStringPoolOffset + resTypeStringPoolByteArraySize
        }


        val bf = ByteBuffer.allocate(newChunkSize)
        bf.takeLittleEndianOrder()
        bf.putShort(header.type)
        bf.putShort(headerSize.toShort())
        bf.putInt(newChunkSize)
        bf.putInt(packageId)
        bf.put(packageName)
        bf.putInt(newResTypeStringPoolOffset)
        bf.putInt(lastPublicType)
        bf.putInt(newResKeywordStringPoolOffset)
        bf.putInt(lastPublicKey)
        bf.putInt(typeIdOffset)
        bf.put(resTypeStringPoolByteArray)
        bf.put(resKeywordStringPoolByteArray)
        resTypesByteArrays.forEach { bf.put(it) }

        return bf.flipToArray()
    }


}