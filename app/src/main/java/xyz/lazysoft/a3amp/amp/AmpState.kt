package xyz.lazysoft.a3amp.amp

import java.util.*

class AmpState(val dump: ByteArray) {

    fun set(index: Int, value: Byte) {
        dump[index] = value
    }

    fun get(index: Int): Byte {
        return dump[index]
    }

    override fun toString(): String {
        return "AmpState(dump=${Arrays.toString(dump)})"
    }


}