package me.xx2bab.polyfill.arsc.base

import me.xx2bab.polyfill.arsc.export.IResArscTweaker
import me.xx2bab.polyfill.arsc.export.SimpleResource
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.LittleEndianOutputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import me.xx2bab.polyfill.arsc.pack.ResPackage
import me.xx2bab.polyfill.arsc.stringpool.StringPool
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer

/**
 * The parser of resource.arsc binary artifact.
 */
class ResTable: IParsable, IResArscTweaker {

    lateinit var header: Header
    var packageCount = 0
    lateinit var stringPool: StringPool
    val packages = mutableListOf<ResPackage>()

    @Throws(IOException::class)
    override fun parse(input: LittleEndianInputStream, start: Long) {
        // 1. Header
        header = Header()
        header.parse(input, start)

        // 2. Package Count
        packageCount = input.readInt()

        // 3. Global StringPool
        stringPool = StringPool()
        stringPool.parse(input, input.filePointer)

        // 4. Package
        for (i in 0 until packageCount) {
            val resPackage = ResPackage()
            resPackage.parse(input, input.filePointer)
            packages.add(resPackage)
        }

//        println("Done")
    }

    override fun toByteArray(): ByteArray {
        val headerSize = header.size() + sizeOf(packageCount)

        val stringPoolByteArray = stringPool.toByteArray()
        val stringPoolSize = stringPoolByteArray.size
        val packageByteArrays = packages.map { it.toByteArray() }
        val packageSize = packageByteArrays.sumBy { it.size }

        val newChunkSize = headerSize + stringPoolSize + packageSize
        val bf = ByteBuffer.allocate(newChunkSize)
        bf.takeLittleEndianOrder()
        bf.putShort(header.type)
        bf.putShort(headerSize.toShort())
        bf.putInt(newChunkSize)
        bf.putInt(packages.size)
        bf.put(stringPoolByteArray)
        packageByteArrays.forEach { bf.put(it) }

        return bf.flipToArray()
    }

    override fun read(source: File) {
        if (source.exists() && source.isFile && source.extension == "arsc") {
            val inputStream = LittleEndianInputStream(source)
            parse(inputStream, 0)
            return
        }
        throw IllegalArgumentException("The arsc file is illegal.")
    }

    override fun write(dest: File) {
        if (dest.exists()) {
            dest.delete()
        }
        dest.parentFile.mkdirs()
        dest.createNewFile()
        val outputStream = LittleEndianOutputStream(dest)
        outputStream.writeByte(toByteArray())
        outputStream.close()
    }

    override fun getResourceTypes(): Map<String, Int> {
        TODO("Not yet implemented")
    }

    override fun findResourceById(id: Int): List<SimpleResource?> {
        TODO("Not yet implemented")
    }

    override fun removeResourceById(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateResourceById(simpleResource: SimpleResource): Boolean {
        TODO("Not yet implemented")
    }

}