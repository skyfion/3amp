package xyz.lazysoft.a3amp.amp

import java.util.*
import kotlin.NoSuchElementException

class PresetDump(val data: ByteArray) {

    var dump: ByteArray = when (data.size) {
        Constants.THR_SYSEX_SIZE -> data.drop(Constants.THR_SYSEX_SHIFT).toByteArray()
        Constants.YDL_DATA -> data.drop(Constants.YDL_DATA_SHIFT).toByteArray()
        else -> throw NoSuchElementException("Unknown dump type")
    }

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
            // this is conflict reverb time and spring reverb, they have one ID
            // and also COMPRESSOR_STOMP_SUSTAIN and COMPRESSOR_RACK_THRESHOLD
            // may by bag inside YDL format
            Constants.REVERB_TIME -> {
                if (get(Constants.REVERB_MODE) == 3.toByte()) {
                    Constants.REVERB_TIME
                } else {
                    Constants.REVERB_SPRING_FILTER
                }
            }
            Constants.COMPRESSOR_STOMP_SUSTAIN -> {
                if (get(Constants.COMPRESSOR_MODE) == 0.toByte()) {
                    Constants.COMPRESSOR_STOMP_SUSTAIN
                } else {
                    listOf(Constants.COMPRESSOR_STOMP_SUSTAIN,
                            Constants.COMPRESSOR_RACK_THRESHOLD)
                }
            }
            else -> if (Constants.TWO_BYTES.contains(id)) listOf(id, id.inc()) else id
        }
    }

    fun writeDump(id: Int, value: Pair<Byte, Byte>) {

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