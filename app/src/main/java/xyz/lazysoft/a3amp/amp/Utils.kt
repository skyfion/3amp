package xyz.lazysoft.a3amp.amp

import java.nio.ByteBuffer

object Utils {

    fun paramToInt(param: ByteArray): Int {
        return ByteBuffer.wrap(param).short.toInt()
    }

    fun intToParam(value: Int): ByteArray {
        return ByteBuffer.wrap(ByteArray(4))
                .putInt(value)
                .array().drop(2).toByteArray()
    }
}