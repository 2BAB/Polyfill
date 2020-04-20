package me.xx2bab.polyfill.arsc.pack.type

import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_BYTE
import me.xx2bab.polyfill.arsc.base.INVALID_VALUE_SHORT
import me.xx2bab.polyfill.arsc.base.IParsable
import me.xx2bab.polyfill.arsc.io.LittleEndianInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Reference [google/android-arscblamer](https://github.com/google/android-arscblamer)
 */
class ResConfig : IParsable {

    companion object {
        /** The minimum size in bytes that a [ResourceConfiguration] can be. */
        private const val MIN_SIZE = 28

        /** The minimum size in bytes that this configuration must be to contain screen config info. */
        private const val SCREEN_CONFIG_MIN_SIZE = 32

        /** The minimum size in bytes that this configuration must be to contain screen dp info. */
        private const val SCREEN_DP_MIN_SIZE = 36

        /** The minimum size in bytes that this configuration must be to contain locale info. */
        private const val LOCALE_MIN_SIZE = 48

        /** The minimum size in bytes that this config must be to contain the screenConfig extension. */
        private const val SCREEN_CONFIG_EXTENSION_MIN_SIZE = 52

        /** The size of resource configurations in bytes for the latest version of Android resources. */
        const val SIZE = SCREEN_CONFIG_EXTENSION_MIN_SIZE


        const val DENSITY_DPI_UNDEFINED = 0
        const val DENSITY_DPI_LDPI = 120
        const val DENSITY_DPI_MDPI = 160
        const val DENSITY_DPI_TVDPI = 213
        const val DENSITY_DPI_HDPI = 240
        const val DENSITY_DPI_XHDPI = 320
        const val DENSITY_DPI_XXHDPI = 480
        const val DENSITY_DPI_XXXHDPI = 640
        const val DENSITY_DPI_ANY = 0xFFFE
        const val DENSITY_DPI_NONE = 0xFFFF

        const val KEYBOARD_NOKEYS = 1
        const val KEYBOARD_QWERTY = 2
        const val KEYBOARD_12KEY = 3

        const val KEYBOARDHIDDEN_MASK = 0x03
        const val KEYBOARDHIDDEN_NO = 1
        const val KEYBOARDHIDDEN_YES = 2
        const val KEYBOARDHIDDEN_SOFT = 3

        const val NAVIGATION_NONAV = 1
        const val NAVIGATION_DPAD = 2
        const val NAVIGATION_TRACKBALL = 3
        const val NAVIGATION_WHEEL = 4
        const val NAVIGATIONHIDDEN_MASK = 0x0C
        const val NAVIGATIONHIDDEN_NO = 0x04
        const val NAVIGATIONHIDDEN_YES = 0x08

        const val SCREENLAYOUT_LAYOUTDIR_MASK = 0xC0
        const val SCREENLAYOUT_LAYOUTDIR_LTR = 0x40
        const val SCREENLAYOUT_LAYOUTDIR_RTL = 0x80
        const val SCREENLAYOUT_LONG_MASK = 0x30
        const val SCREENLAYOUT_LONG_NO = 0x10
        const val SCREENLAYOUT_LONG_YES = 0x20
        const val SCREENLAYOUT_ROUND_MASK = 0x03
        const val SCREENLAYOUT_ROUND_NO = 0x01
        const val SCREENLAYOUT_ROUND_YES = 0x02
        const val SCREENLAYOUT_SIZE_MASK = 0x0F
        const val SCREENLAYOUT_SIZE_SMALL = 0x01
        const val SCREENLAYOUT_SIZE_NORMAL = 0x02
        const val SCREENLAYOUT_SIZE_LARGE = 0x03
        const val SCREENLAYOUT_SIZE_XLARGE = 0x04

        const val TOUCHSCREEN_NOTOUCH = 1
        const val TOUCHSCREEN_FINGER = 3

        const val UI_MODE_NIGHT_MASK = 0x30
        const val UI_MODE_NIGHT_NO = 0x10
        const val UI_MODE_NIGHT_YES = 0x20

        const val UI_MODE_TYPE_MASK = 0x0F
        const val UI_MODE_TYPE_DESK = 0x02
        const val UI_MODE_TYPE_CAR = 0x03
        const val UI_MODE_TYPE_TELEVISION = 0x04
        const val UI_MODE_TYPE_APPLIANCE = 0x05
        const val UI_MODE_TYPE_WATCH = 0x06
        const val UI_MODE_TYPE_VR_HEADSET = 0x07


        /** The below constants are from android.content.res.Configuration.  */
        const val COLOR_MODE_WIDE_COLOR_GAMUT_MASK = 0x03
        const val COLOR_MODE_WIDE_COLOR_GAMUT_UNDEFINED = 0
        const val COLOR_MODE_WIDE_COLOR_GAMUT_NO = 0x01
        const val COLOR_MODE_WIDE_COLOR_GAMUT_YES = 0x02
        const val COLOR_MODE_HDR_MASK = 0x0C
        const val COLOR_MODE_HDR_UNDEFINED = 0
        const val COLOR_MODE_HDR_NO = 0x04
        const val COLOR_MODE_HDR_YES = 0x08
    }


    var start: Long = -1
    var configSize: Int = 0
//    lateinit var config: ByteArray

    var mcc: Short = INVALID_VALUE_SHORT
    var mnc: Short = INVALID_VALUE_SHORT

    val language = ByteArray(2)
    val region = ByteArray(2)
    var orientation: Byte = INVALID_VALUE_BYTE
    var touchScreen: Byte = INVALID_VALUE_BYTE
    var density: Short = INVALID_VALUE_SHORT
    var keyboard: Byte = INVALID_VALUE_BYTE
    var navigation: Byte = INVALID_VALUE_BYTE
    var inputFlags: Byte = INVALID_VALUE_BYTE

    var reservedPadding0: Byte = INVALID_VALUE_BYTE

    var screenWidth: Short = INVALID_VALUE_SHORT
    var screenHeight: Short = INVALID_VALUE_SHORT
    var sdkVersion: Short = INVALID_VALUE_SHORT
    var minorVersion: Short = INVALID_VALUE_SHORT
    var screenLayout: Byte = INVALID_VALUE_BYTE
    var uiMode: Byte = INVALID_VALUE_BYTE
    var smallestScreenWidthDp: Short = INVALID_VALUE_SHORT
    var screenWidthDp: Short = INVALID_VALUE_SHORT
    var screenHeightDp: Short = INVALID_VALUE_SHORT

    val localeScript = ByteArray(4)
    val localeVariant = ByteArray(8)

    var screenLayout2: Byte = INVALID_VALUE_BYTE
    var colorMode: Byte = INVALID_VALUE_BYTE
    var reservedPadding1: Short = INVALID_VALUE_SHORT

    lateinit var unknownPadding0: ByteArray

    override fun parse(input: LittleEndianInputStream, start: Long) {
        this.start = start
        configSize = input.readInt()
        if (configSize > 4) {
            // Deduct the Int size of size itself
//            config = ByteArray(configSize - 4)
//            input.read(config)

            mcc = input.readShort()
            mnc = input.readShort()

            input.read(language)
            input.read(region)
            orientation = input.readByte()
            touchScreen = input.readByte()
            density = input.readShort()
            keyboard = input.readByte()
            navigation = input.readByte()
            inputFlags = input.readByte()

            reservedPadding0 = input.readByte()

            screenWidth = input.readShort()
            screenHeight = input.readShort()
            sdkVersion = input.readShort()
            minorVersion = input.readShort()
            if (configSize >= SCREEN_CONFIG_MIN_SIZE) {
                screenLayout = input.readByte()
                uiMode = input.readByte()
                smallestScreenWidthDp = input.readShort()
            }
            if (configSize >= SCREEN_DP_MIN_SIZE) {
                screenWidthDp = input.readShort()
                screenHeightDp = input.readShort()
            }

            if (configSize >= LOCALE_MIN_SIZE) {
                input.read(localeScript)
                input.read(localeVariant)
            }

            if (configSize >= SCREEN_CONFIG_EXTENSION_MIN_SIZE) {
                screenLayout2 = input.readByte()
                colorMode = input.readByte()
                reservedPadding1 = input.readShort()
            }

            unknownPadding0 = ByteArray(configSize - (input.filePointer - start).toInt())
            input.read(unknownPadding0)

//            input.seek(start)
//            val origin = ByteArray(configSize)
//            input.read(origin)
//            val out = toByteArray()
//            val res = origin.contentEquals(out)
//            println(res)
//            input.seek(start + configSize)
        }
    }

    override fun toByteArray(): ByteArray {
        val bf = ByteBuffer.allocate(configSize)
        bf.order(ByteOrder.LITTLE_ENDIAN)
        bf.clear()

        bf.putInt(configSize)
        bf.putShort(mcc)
        bf.putShort(mnc)

        bf.put(language)
        bf.put(region)
        bf.put(orientation)
        bf.put(touchScreen)
        bf.putShort(density)
        bf.put(keyboard)
        bf.put(navigation)
        bf.put(inputFlags)

        bf.put(reservedPadding0)

        bf.putShort(screenWidth)
        bf.putShort(screenHeight)
        bf.putShort(sdkVersion)
        bf.putShort(minorVersion)
        if (configSize >= SCREEN_CONFIG_MIN_SIZE) {
            bf.put(screenLayout)
            bf.put(uiMode)
            bf.putShort(smallestScreenWidthDp)
        }
        if (configSize >= SCREEN_DP_MIN_SIZE) {
            bf.putShort(screenWidthDp)
            bf.putShort(screenHeightDp)
        }

        if (configSize >= LOCALE_MIN_SIZE) {
            bf.put(localeScript)
            bf.put(localeVariant)
        }

        if (configSize >= SCREEN_CONFIG_EXTENSION_MIN_SIZE) {
            bf.put(screenLayout2)
            bf.put(colorMode)
            bf.putShort(reservedPadding1)
        }

        bf.put(unknownPadding0)

        bf.flip()
        return bf.array()
    }

}