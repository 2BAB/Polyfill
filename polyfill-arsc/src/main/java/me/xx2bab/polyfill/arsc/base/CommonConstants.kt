package me.xx2bab.polyfill.arsc.base

const val INVALID_VALUE_BYTE: Byte = (Byte.MIN_VALUE + 1).toByte()
const val INVALID_VALUE_SHORT: Short = (Short.MIN_VALUE + 1).toShort()
const val INVALID_VALUE_INT: Int = Int.MIN_VALUE + 1

const val UTF8_FLAG = 1 shl 8

const val RES_TABLE_TYPE_SPEC_TYPE: Short = 0x0202
const val RES_TABLE_TYPE_TYPE: Short = 0x0201

const val NO_ENTRY_INDEX = 0xFFFFFFFF

const val RES_TABLE_ENTRY_FLAG_COMPLEX: Short = 0x0001
const val RES_TABLE_ENTRY_FLAG_PUBLIC: Short = 0x0002

const val SIZE_INT = 4
const val SIZE_SHORT = 2
const val SIZE_BYTE = 1
const val SIZE_CHAR = 2
const val SIZE_LONG = 8
const val SIZE_FLOAT = 4
const val SIZE_DOUBLE = 8

fun sizeOf(data: Any?): Int {
    if (data == null) throw NullPointerException()
    val dataType = data.javaClass
    return when (data) {
        is Int -> SIZE_INT
        is Short -> SIZE_SHORT
        is Byte -> SIZE_BYTE
        is Char -> SIZE_CHAR
        is Long -> SIZE_LONG
        is Float -> SIZE_FLOAT
        is Double -> SIZE_DOUBLE
        else -> 0
    }
}