package me.xx2bab.polyfill.arsc

import me.xx2bab.polyfill.arsc.base.ResTable
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.pack.TypeType
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.io.File

class ResourceTableIntegrationTest {

    @Test
    fun simpleARSCTest() {
        val input = ResTable.fileToLittleEndianInputStream(File("/Users/2bab/Desktop/resources.arsc"))
        val resTable = ResTable()
        resTable.parse(input, 0)

        validateResConfigs(input, resTable)
        validateResEntries(input, resTable)
        validateTypes(input, resTable)
        validateStringPools(input, resTable)
        validatePackages(input, resTable)
        validateTable(input, resTable)
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
}