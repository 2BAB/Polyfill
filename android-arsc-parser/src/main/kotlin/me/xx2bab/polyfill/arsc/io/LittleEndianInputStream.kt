package me.xx2bab.polyfill.arsc.io

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Convert from Matrix repository.
 *
 * https://github.com/Tencent/matrix/blob/master/matrix/matrix-android/matrix-arscutil/src/main/java/com/tencent/mm/arscutil/io/LittleEndianInputStream.java
 *
 * Created by jinqiuchen on 18/7/29.
 */
class LittleEndianInputStream(private val original: RandomAccessFile) : InputStream() {

    constructor(file: File) : this(RandomAccessFile(file, "r")) {}

    constructor(file: String) : this(RandomAccessFile(file, "r")) {}

    @Throws(IOException::class)
    override fun read(): Int {
        // TODO Auto-generated method stub
        return original.read()
    }

    @Throws(IOException::class)
    fun readShort(): Short {
        val byteBuffer = ByteBuffer.allocate(2)
        byteBuffer.clear()
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        byteBuffer.put(original.readByte())
        byteBuffer.put(original.readByte())
        byteBuffer.flip()
        return byteBuffer.short
    }

    @Throws(IOException::class)
    fun readInt(): Int {
        val byteBuffer = ByteBuffer.allocate(4)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        byteBuffer.clear()
        for (i in 1..4) {
            byteBuffer.put(original.readByte())
        }
        byteBuffer.flip()
        return byteBuffer.int
    }

    @Throws(IOException::class)
    fun readByte(): Byte {
        return original.readByte()
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun readByte(buffer: ByteArray, offset: Int = 0, length: Int = buffer.size) {
        val byteBuffer = ByteBuffer.allocate(length)
        byteBuffer.clear()
        for (i in 1..length) {
            byteBuffer.put(original.readByte())
        }
        byteBuffer.flip()
        byteBuffer[buffer, offset, length]
    }

    @Throws(IOException::class)
    fun seek(pos: Long) {
        original.seek(pos)
    }

    @get:Throws(IOException::class)
    val filePointer: Long
        get() = original.filePointer

    @get:Throws(IOException::class)
    val fileLength: Long
        get() = original.length()

    @Throws(IOException::class)
    override fun close() {
        // TODO Auto-generated method stub
        super.close()
        original.close()
    }

}