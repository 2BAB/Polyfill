package me.xx2bab.polyfill.arsc

import com.google.common.io.Resources.getResource
import me.xx2bab.polyfill.arsc.base.ResTable
import me.xx2bab.polyfill.arsc.export.SimpleResource
import me.xx2bab.polyfill.arsc.export.SupportedResConfig
import me.xx2bab.polyfill.arsc.export.SupportedResType
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.pack.TypeType
import me.xx2bab.polyfill.arsc.stringpool.UtfUtil
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class ResourceTableIntegrationTest {

    @Test
    fun simpleARSCTest() {
        val originArscFile = File(getResource("resources.arsc").toURI())
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
    fun findResByIdTest_Regular() {
        val originArscFile = File(getResource("resources.arsc").toURI())
        val resTable = ResTable()
        resTable.read(originArscFile)

        val result = resTable.findResourceById(0x7f040036)
        assertEquals(result[0]!!.value!!, "#ff80cbc4")
    }

    @Test
    fun updateResByIdTest_DefaultConfig() {
        // Write
        val originArscFile = File(getResource("resources.arsc").toURI())
        val resTable = ResTable()
        resTable.read(originArscFile)

        val result = resTable.updateResourceById(SimpleResource(0x7f0b0027,
                SupportedResType.STRING,
                // It doesn't matter if you pass a null or empty string when update,
                // since we only do locating by id
                "app_name",
                // To change the app name to SP2
                "SP2"),
                // A default config without any customization
                SupportedResConfig())
        assertTrue(result)
        val result2 = resTable.updateResourceById(SimpleResource(0x7f040027,
                SupportedResType.COLOR,
                // It doesn't matter if you pass a null or empty string when update,
                // since we only do locating by id
                "colorPrimary",
                // To change the colorPrimary to Red
                "#ff450d"),
                // A default config without any customization
                SupportedResConfig())
        assertTrue(result2)

        // Read
        val generatedArscFile = File(originArscFile.parentFile,
                "${originArscFile.nameWithoutExtension}-modified.arsc")
        resTable.write(generatedArscFile)
        val newResTable = ResTable()
        newResTable.read(generatedArscFile)
        val appNameChangeResult = newResTable.findResourceById(0x7f0b0027)
        assertEquals(appNameChangeResult[0]!!.value, "SP2")
        val colorPrimaryChangeResult = newResTable.findResourceById(0x7f040027)
        assertEquals(colorPrimaryChangeResult[0]!!.value, "#ffff450d")
        generatedArscFile.delete()
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