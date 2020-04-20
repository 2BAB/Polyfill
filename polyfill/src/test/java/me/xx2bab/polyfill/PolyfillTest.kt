package me.xx2bab.polyfill

import me.xx2bab.polyfill.arsc.export.IResArscTweaker
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PolyfillTest {

    @Test
    fun getServiceTest_Regular() {
        val polyfill = Polyfill()
        assertNotNull(polyfill.getService(IResArscTweaker::class.java))
    }

    @Test
    fun getServiceTest_IsNull() {
        val polyfill = Polyfill()
        assertNull(polyfill.getService(PolyfillTest::class.java))
    }


}