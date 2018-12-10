package xyz.lazysoft.a3amp.amp

import java.nio.ByteBuffer

object Utils {

    fun byteArrayOf(vararg a: Int): ByteArray {
        return if (a.isNotEmpty())
            a.map { i -> i.toByte() }.toByteArray()
        else
            ByteArray(0)
    }

    fun paramToInt(param: ByteArray): Int {
        return ByteBuffer.wrap(param).short.toInt()
    }

    fun intToParam(value: Int): ByteArray {
        return ByteBuffer.wrap(ByteArray(4))
                .putInt(value)
                .array().drop(2).toByteArray()
    }
}