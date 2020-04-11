package me.xx2bab.polyfill.arsc.base

import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.pack.ResPackage
import me.xx2bab.polyfill.arsc.stringpool.StringPool
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException

/**
 * The parser of resource.arsc binary artifact.
 */
class ResTable: IParsable {

    companion object {
        @JvmStatic
        fun main(args : Array<String>) {
            val input = fileToLittleEndianInputStream(File("/Users/2bab/Desktop/resources.arsc"))
            val resTable = ResTable()
            resTable.parse(input, 0)
        }

        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun fileToLittleEndianInputStream(arscFile: File): LittleEndianInputStream {
            if (arscFile.exists() && arscFile.isFile && arscFile.extension == "arsc") {
                return LittleEndianInputStream(arscFile)
            }
            throw IllegalArgumentException("The arsc file is illegal.")
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

        println("Done")
    }

}