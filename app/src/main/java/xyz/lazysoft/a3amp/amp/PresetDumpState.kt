package xyz.lazysoft.a3amp.amp

import java.util.*

class PresetDumpState(val dump: ByteArray) {

    fun set(index: Int, value: Byte) {
        dump[index] = value
    }

    fun get(index: Int): Byte {
        return dump[index]
    }

    override fun toString(): String {
        return "PresetDumpState(dump=${Arrays.toString(dump)})"
    }


    fun writeDump(id: Int, value: Pair<Byte, Byte>) {
        // this is conflict reverb time and spring reverb, they have one ID
        // and also COMPRESSOR_STOMP_SUSTAIN and COMPRESSOR_RACK_THRESHOLD
        // maybe is a mistake inside YDL format
        val cell = when (id) {
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

        when (cell) {
            is List<*> -> {
                cell as List<Int>
                set(cell[0], value.first)
                set(cell[1], value.second)
            }
            is Int -> set(cell, value.second)

        }
    }
}