package me.xx2bab.polyfill.arsc

import me.xx2bab.polyfill.arsc.base.ResTable
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.pack.TypeType
import me.xx2bab.polyfill.arsc.stringpool.UtfUtil
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class ResourceTableIntegrationTest {

    @Test
    fun simpleARSCTest() {
        val originArscFile = File("/Users/2bab/Desktop/resources.arsc")
        val input = LittleEndianInputStream(originArscFile) // Used for validation
        val resTable = ResTable()
        resTable.read(originArscFile)

        validateStrings(resTable)
        validateResConfigs(input, resTable)
        validateResEntries(input, resTable)
        validateTypes(input, resTable)
        validateStringPools(input, resTable)
        validatePackages(input, resTable)
        validateTable(input, resTable)
        validateFile(originArscFile, resTable)
    }

    @Test
    fun findResByIdTest() {
        val originArscFile = File("/Users/2bab/Desktop/resources.arsc")
        val resTable = ResTable()
        resTable.read(originArscFile)

        val result = resTable.findResourceById(0x7f040036)
        assertEquals(Integer.toHexString(result[0]!!.value.toInt()), "ff80cbc4")
    }

    private fun validateStrings(resTable: ResTable) {
        var byteCount = 0
        resTable.stringPool.stringByteArrays.forEachIndexed { index, element ->
            byteCount += validateString(element, resTable.stringPool.flag,
                    index == resTable.stringPool.stringByteArrays.size - 1, byteCount)
        }
        resTable.packages.forEach { pack ->
            byteCount = 0
            pack.resTypeStringPool.stringByteArrays.forEachIndexed { index, element ->
                byteCount += validateString(element, pack.resTypeStringPool.flag,
                        index == pack.resTypeStringPool.stringByteArrays.size - 1, byteCount)
            }
            byteCount = 0
            pack.resKeywordStringPool.stringByteArrays.forEachIndexed { index, element ->
                byteCount += validateString(element, pack.resKeywordStringPool.flag,
                        index == pack.resKeywordStringPool.stringByteArrays.size - 1, byteCount)
            }
        }
    }

    private fun validateString(it: ByteArray, flag: Int, isLastItem: Boolean, byteCount: Int): Int {
        val string = UtfUtil.byteArrayToString(it, flag)
        val byteArray = UtfUtil.stringToByteArray(string, flag)

        if (isLastItem) {
            val zeroCount = 4 - (byteCount + byteArray.size) % 4 // 4 bytes aligned
            val origin = if (zeroCount != 4) {
                it.take(it.size - zeroCount).toByteArray()
            } else {
                it
            }
            assertArrayEquals(origin, byteArray)
        } else {
            assertArrayEquals(it, byteArray)
        }

        return byteArray.size
    }

    private fun validateResConfigs(input: LittleEndianInputStream, resTable: ResTable) {
        resTable.packages.forEach { pack ->
            pack.resTypes.forEach {
                if (it is TypeType) {
                    input.seek(it.config.start)
                    val configByteArrayInput = ByteArray(it.config.configSize)
                    input.read(configByteArrayInput)
                    val configByteArrayOutput = it.config.toByteArray()
                    assertArrayEquals(configByteArrayInput, configByteArrayOutput)
                }
            }
        }
    }

    private fun validateResEntries(input: LittleEndianInputStream, resTable: ResTable) {
        resTable.packages.forEach { pack ->
            pack.resTypes.forEach { type ->
                if (type is TypeType) {
                    type.entries.forEach { entry ->
                        if (entry != null) {
                            val entryByteArrayOutput = entry.toByteArray()
                            val size = entryByteArrayOutput.size
                            input.seek(entry.start)
                            val entryByteArrayInput = ByteArray(size)
                            input.read(entryByteArrayInput)
                            assertArrayEquals(entryByteArrayInput, entryByteArrayOutput)
                        }
                    }
                }
            }
        }
    }

    private fun validateTypes(input: LittleEndianInputStream, resTable: ResTable) {
        resTable.packages.forEach { pack ->
            pack.resTypes.forEach { type ->
                // Validate both TypeType and TypeSpec
                val typeByteArrayOutput = type.toByteArray()
                input.seek(type.header.start)
                val typeByteArrayInput = ByteArray(typeByteArrayOutput.size)
                input.read(typeByteArrayInput)
                assertArrayEquals(typeByteArrayInput, typeByteArrayOutput)
//                println(typeByteArrayInput.contentEquals(typeByteArrayOutput))
            }
        }
    }

    private fun validateStringPools(input: LittleEndianInputStream, resTable: ResTable) {
        resTable.packages.forEach { pack ->
            val resTypeStringPoolOutput = pack.resTypeStringPool.toByteArray()
            val resTypeStringPoolInput = ByteArray(resTypeStringPoolOutput.size)
            input.seek(pack.header.start + pack.resTypeStringPoolOffset.toLong())
            input.read(resTypeStringPoolInput)
            assertArrayEquals(resTypeStringPoolInput, resTypeStringPoolOutput)
        }
    }


    private fun validatePackages(input: LittleEndianInputStream, resTable: ResTable) {
        resTable.packages.forEach { pack ->
            val packageOutput = pack.toByteArray()
            val packageInput = ByteArray(pack.header.chunkSize)
            input.seek(pack.header.start)
            input.read(packageInput)
            assertArrayEquals(packageInput, packageOutput)
        }
    }

    private fun validateTable(input: LittleEndianInputStream, resTable: ResTable) {
        val tableOutput = resTable.toByteArray()
        val tableInput = ByteArray(resTable.header.chunkSize)
        input.seek(0)
        input.read(tableInput)
        assertArrayEquals(tableInput, tableOutput)
    }

    private fun validateFile(originArscFile: File, resTable: ResTable) {
        val generatedArscFile = File(originArscFile.parentFile,
                "${originArscFile.nameWithoutExtension}-modified.arsc")
        resTable.write(generatedArscFile)
        assertArrayEquals(Files.readAllBytes(Paths.get(originArscFile.absolutePath)),
                Files.readAllBytes(Paths.get(generatedArscFile.absolutePath)))
        generatedArscFile.delete()
    }


}