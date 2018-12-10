package xyz.lazysoft.a3amp

import org.junit.Assert
import org.junit.Test
import xyz.lazysoft.a3amp.amp.Utils
import xyz.lazysoft.a3amp.amp.Utils.byteArrayOf

class UtilsTest {

    @Test
    fun intToParam() {
        Assert.assertArrayEquals(Utils.intToParam(256), byteArrayOf(0x01, 0x00))
        Assert.assertArrayEquals(Utils.intToParam(200), byteArrayOf(0x00, 0xC8))
    }

    @Test
    fun paramToInt() {
        Assert.assertEquals(Utils.paramToInt(byteArrayOf(0x00, 0xA2)), 162)
        Assert.assertEquals(Utils.paramToInt(byteArrayOf(0x01, 0x41)), 321)
    }
}