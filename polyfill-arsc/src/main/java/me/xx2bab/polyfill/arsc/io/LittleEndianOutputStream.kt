package me.xx2bab.polyfill.arsc.io

import java.io.IOException
import java.io.OutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Convert from Matrix repository.
 *
 * https://github.com/Tencent/matrix/blob/master/matrix/matrix-android/matrix-arscutil/src/main/java/com/tencent/mm/arscutil/io/LittleEndianOutputStream.java
 *
 * Created by jinqiuchen on 18/7/29.
 */
class LittleEndianOutputStream(private val original: RandomAccessFile) : OutputStream() {

    constructor(file: String?) : this(RandomAccessFile(file, "rw")) {}

    @Throws(IOException::class)
    override fun write(b: Int) {
        original.write(b)
    }

    @Throws(IOException::class)
    fun writeShort(data: Short) {
        val byteBuffer = ByteBuffer.allocate(2)
        byteBuffer.clear()
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        byteBuffer.putShort(data)
        byteBuffer.flip()
        original.write(byteBuffer.array())
    }

    @Throws(IOException::class)
    fun writeInt(data: Int) {
        val byteBuffer = ByteBuffer.allocate(4)
        byteBuffer.clear()
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        byteBuffer.putInt(data)
        byteBuffer.flip()
        original.write(byteBuffer.array())
    }

    @Throws(IOException::class)
    fun writeByte(data: Byte) {
        original.write(data.toInt())
    }

    @Throws(IOException::class)
    fun writeByte(buffer: ByteArray?) {
        original.write(buffer)
    }

    @Throws(IOException::class)
    fun writeByte(buffer: ByteArray?, offset: Int, length: Int) {
        original.write(buffer, offset, length)
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
        super.close()
        original.close()
    }

}