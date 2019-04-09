package xyz.lazysoft.a3amp.amp

import java.util.*

class PresetDump(val dump: ByteArray) {

    fun set(index: Int, value: Byte) {
        dump[index] = value
    }

    fun get(index: Int): Byte {
        return dump[index]
    }

    // todo maybe return int?
    fun getValueById(id: Int): ByteArray? {
        val cell = calcCell(id)
        return when (cell) {
            is List<*> -> {
                cell as List<Int>
                byteArrayOf(get(cell[0]), get(cell[1]))
            }
            is Int -> byteArrayOf(0.toByte(), get(cell))
            else -> null
        }
    }

    private fun calcCell(id: Int): Any? {
        return when (id) {
            Constants.REVERB_TIME -> {
                val mode = Constants.DUMP_MAP[Constants.REVERB_MODE] as Int
                if (get(mode) == 3.toByte()) {
                    193
                } else {
                    194
                }
            }
            Constants.COMPRESSOR_STOMP_SUSTAIN -> {
                val mode = Constants.DUMP_MAP[Constants.COMPRESSOR_MODE] as Int
                if (get(mode) == 0.toByte()) {
                    145
                } else {
                    listOf(145, 146)
                }
            }
            else -> Constants.DUMP_MAP[id]
        }
    }

    fun writeDump(id: Int, value: Pair<Byte, Byte>) {
        // this is conflict reverb time and spring reverb, they have one ID
        // and also COMPRESSOR_STOMP_SUSTAIN and COMPRESSOR_RACK_THRESHOLD
        // may by bag inside YDL format
        val cell = calcCell(id)

        when (cell) {
            is List<*> -> {
                cell as List<Int>
                set(cell[0], value.first)
                set(cell[1], value.second)
            }
            is Int -> set(cell, value.second)

        }
    }

    override fun toString(): String {
        return Arrays.toString(dump)
    }
}