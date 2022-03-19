package me.xx2bab.polyfill.arsc.base

import android.util.TypedValue.*
import me.xx2bab.polyfill.arsc.export.IResArscTweaker
import me.xx2bab.polyfill.arsc.export.SimpleResource
import me.xx2bab.polyfill.arsc.export.SupportedResConfig
import me.xx2bab.polyfill.arsc.export.SupportedResType
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import me.xx2bab.polyfill.arsc.io.LittleEndianOutputStream
import me.xx2bab.polyfill.arsc.io.flipToArray
import me.xx2bab.polyfill.arsc.io.takeLittleEndianOrder
import me.xx2bab.polyfill.arsc.pack.ResPackage
import me.xx2bab.polyfill.arsc.pack.TypeType
import me.xx2bab.polyfill.arsc.pack.type.ResEntry
import me.xx2bab.polyfill.arsc.stringpool.StringPool
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.util.*

/**
 * The parser of resource.arsc binary artifact.
 */
class ResTable : IParsable, IResArscTweaker {

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
        return Collections.emptyMap()
    }

    override fun findResourceById(id: Int): List<SimpleResource?> {
        val filteredPackages = packages.filter { it.packageId == getPackageId(id) }
        return findResourceEntriesById(id, SupportedResConfig()).map {
            val type = parseSupportType(it.resValue.dataType.toInt())
            val name = parseName(filteredPackages[0], it.stringPoolIndex)
            val value = parseValue(type, it.resValue.data)
            SimpleResource(id, type, name, value)
        }
    }

    override fun removeResourceById(id: Int): Boolean {
        return false
    }

    override fun updateResourceById(resource: SimpleResource,
                                    config: SupportedResConfig): Boolean {
        val entries = findResourceEntriesById(resource.id, config)
        if (entries.isEmpty()) {
            return false
        }
        when (resource.type) {
            SupportedResType.COLOR -> {
                entries.forEach {
                    it.resValue.data = parseColor(resource.value!!)
                }
            }

            SupportedResType.STRING -> {
                entries.forEach {
                    stringPool.strings[it.resValue.data] = resource.value
                }
            }

            SupportedResType.UNSUPPORTED -> return false
        }
        return true
    }

    private fun findResourceEntriesById(id: Int, config: SupportedResConfig): List<ResEntry> {
        val packageId = getPackageId(id)
        val typeId = getResourceTypeId(id)
        val entryId = getResourceEntryId(id)
        val filteredPackages = packages.filter { it.packageId == packageId }
        if (filteredPackages.isNullOrEmpty()) {
            return Collections.emptyList()
        }
        val filteredTypes = filteredPackages[0].resTypes.filter { it.typeId.toInt() == typeId }
        if (filteredTypes.isNullOrEmpty()) {
            return Collections.emptyList()
        }
        return filteredTypes.filter {
            it is TypeType && it.entries.size > entryId // List<TypeType>
        }.filter {
            val tt = it as TypeType
            val result1 = if (config.minOsVersion != INVALID_VALUE_INT) {
                val v = tt.config.sdkVersion.toInt()
                if (v == 0) {
                    false
                } else {
                    config.minOsVersion >= v
                }
            } else {
                true
            }
            val result2 = if (config.language.isNotBlank()) {
                val lang = tt.config.unpackLanguage(tt.config.language)
                if (lang == "") {
                    false
                } else {
                    config.language.equals(lang, true)
                }
            } else {
                true
            }
            result1 && result2
        }.mapNotNull {
            (it as TypeType).entries[entryId] // List<ResEntry>
        }.filter {
            it.pairCount == 0 // List<ResEntry>, here only support simple ResValue
        }.filter {
            val type = it.resValue.dataType.toInt()
            parseSupportType(type) != SupportedResType.UNSUPPORTED
        }
    }

    private fun parseSupportType(type: Int): SupportedResType {
        return when (type) {
            in TYPE_FIRST_COLOR_INT..TYPE_LAST_COLOR_INT -> {
                SupportedResType.COLOR
            }
            TYPE_STRING -> {
                SupportedResType.STRING
            }
            else -> {
                SupportedResType.UNSUPPORTED
            }
        }
    }

    private fun parseName(resPackage: ResPackage, nameIndex: Int): String? {
        return resPackage.resKeywordStringPool.strings[nameIndex]
    }

    private fun parseValue(type: SupportedResType, value: Int): String? {
        if (type == SupportedResType.COLOR) { // Color
            return "#${Integer.toHexString(value)}"
        } else if (type == SupportedResType.STRING) {
            return stringPool.strings[value]
        }
        return ""
    }


    private fun getPackageId(resourceId: Int): Int {
        return resourceId and 0xFF000000.toInt() shr 24
    }

    private fun getResourceTypeId(resourceId: Int): Int {
        return resourceId and 0x00FF0000 shr 16
    }

    private fun getResourceEntryId(resourceId: Int): Int {
        return resourceId and 0x0000FFFF
    }

    private fun generateResourceId(packageId: Int, typeId: Int, entryId: Int): Int {
        return (packageId shl 24) + (typeId shl 16) + (entryId)
    }

    /**
     * Converted from Android Source Code [android.graphic.Color#parseColor(color: String)]
     * [Source Link](https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/java/android/graphics/Color.java?q=android.graphics.Color)
     */
    private fun parseColor(colorString: String): Int {
        if (colorString[0] == '#') {
            // Use a long to avoid rollovers on #ffXXXXXX
            var color = colorString.substring(1).toLong(16)
            if (colorString.length == 7) {
                // Set the alpha value
                color = color or -0x1000000
            } else require(colorString.length == 9) { "Unknown color" }
            return color.toInt()
        }
        throw java.lang.IllegalArgumentException("Unknown color")
    }

}