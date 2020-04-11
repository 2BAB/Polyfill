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