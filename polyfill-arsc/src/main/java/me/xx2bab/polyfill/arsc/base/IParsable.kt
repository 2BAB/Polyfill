package me.xx2bab.polyfill.arsc.base

import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import java.io.IOException

/**
 * To denote a class requires parsing its properties from byte streams.
 */
interface IParsable {

    /**
     * Do not call this function in a constructor since that is a bad practice to throw exception in constructors.
     *
     * Reference: https://stackoverflow.com/questions/6086334/is-it-good-practice-to-make-the-constructor-throw-an-exception/6086399
     */
    @Throws(IOException::class)
    fun parse(input: LittleEndianInputStream, start: Long)

}