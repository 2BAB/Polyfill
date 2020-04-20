package me.xx2bab.polyfill.arsc.base

import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.LittleEndianOutputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import me.xx2bab.polyfill.arsc.pack.ResPackage
import me.xx2bab.polyfill.arsc.stringpool.StringPool
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException
import java.nio.ByteBuffer

/**
 * The parser of resource.arsc binary artifact.
 */
class ResTable: IParsable {

    companion object {
        @JvmStatic
        @Throws(IllegalArgumentException::class, IOException::class)
        fun fileToLittleEndianInputStream(arscFile: File): LittleEndianInputStream {
            if (arscFile.exists() && arscFile.isFile && arscFile.extension == "arsc") {
                return LittleEndianInputStream(arscFile)
            }
            throw IllegalArgumentException("The arsc file is illegal.")
        }
        @JvmStatic
        @Throws(IOException::class)
        fun byteArrayToFile(byteArray: ByteArray, file: File) {
            if (file.exists()) {
                file.delete()
            }
            file.parentFile.mkdirs()
            file.createNewFile()
            val outputStream = LittleEndianOutputStream(file)
            outputStream.writeByte(byteArray)
            outputStream.close()
        }
    }

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

}