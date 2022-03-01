package me.xx2bab.polyfill.arsc.io

import java.nio.ByteBuffer
import java.nio.ByteOrder

fun ByteBuffer.takeLittleEndianOrder() {
    order(ByteOrder.LITTLE_ENDIAN)
    clear()
}

fun ByteBuffer.flipToArray(): ByteArray {
    flip()
    return array()
}
