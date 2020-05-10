package me.xx2bab.polyfill.manifest.post

import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.base.SIZE_INT
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import java.nio.ByteBuffer

class ResourceIdBlock: IParsable {

    lateinit var header: Header
    lateinit var idArray: IntArray

    override fun parse(input: LittleEndianInputStream, start: Long) {
        input.seek(start)

        header = Header()
        header.parse(input, start)

        val resourceIdChunkCount = (header.chunkSize - header.size()) / 4
        idArray = IntArray(resourceIdChunkCount)
        for (i in 0 until resourceIdChunkCount) {
            idArray[i] = input.readInt()
        }
    }

    override fun toByteArray(): ByteArray {
        val newChunkSize = header.size() + idArray.size * SIZE_INT
        val bf = ByteBuffer.allocate(newChunkSize)
        bf.takeLittleEndianOrder()

        bf.putInt(header.chunkType)
        bf.putInt(newChunkSize)
        idArray.forEach { bf.putInt(it) }
        
        return bf.flipToArray()
    }


}