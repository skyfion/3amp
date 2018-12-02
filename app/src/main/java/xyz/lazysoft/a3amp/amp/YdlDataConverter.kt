package xyz.lazysoft.a3amp.amp

import xyz.lazysoft.a3amp.amp.Constants.Companion as C

/**
 * Yamaha thr ydl file reader/converter
 * */
class YdlDataConverter {
    companion object {
        val THR_DUMP_SIZE = 276
        val THR_DUMP_OFFSET = 18
        val THR_FILE_SIZE = 265
        val THR_FILE_OFFSET = 9
        val THR_SYSEX_SIZE = 256
        val THR_SETTINGS_NAME_SIZE = 64

        private val dumpMap = hashMapOf(
                C.AMP to 128,
                C.K_GAIN to 129,
                C.K_MASTER to 130,
                C.K_BASS to 131,
                C.K_MID to 132,
                C.K_TREB to 133,
                C.CAB to 134,
                C.COMPRESSOR_SW to 159,
                C.COMPRESSOR_STOMP_SUSTAIN to 145,
                C.COMPRESSOR_STOMP_OUTPUT to 146,
                C.COMPRESSOR_RACK_THRESHOLD to listOf(145, 146),
                C.COMPRESSOR_RACK_ATTACK to 147,
                C.COMPRESSOR_RACK_RELEASE to 148,
                C.COMPRESSOR_RACK_RATIO to 149,
                C.COMPRESSOR_RACK_KNEE to 150,
                C.COMPRESSOR_RACK_OUTPUT to listOf(151, 152),
                C.EFFECTS_SW to 175,
                C.EFFECTS_MODE to 160,
                C.EFFECT_KNOB1 to 161,
                C.EFFECT_KNOB2 to 162,
                C.EFFECT_KNOB3 to 163,
                C.EFFECT_KNOB4 to 164,
                C.EFFECT_KNOB5 to 165,
                C.DELAY_TIME to listOf(177, 178),
                C.DELAY_FEEDBACK to 179,
                C.DELAY_HIGH_CUT to listOf(180, 181),
                C.DELAY_LOW_CUT to listOf(182, 183),
                C.DELAY_LEVEL to 184,
                C.DELAY_SW to 191,
                C.REVERB_SW to 207,
                C.REVERB_MODE to 192,
                C.REVERB_TIME to listOf(193, 194),
                C.REVERB_PRE_DELAY to listOf(195, 196),
                C.REVERB_LOW_CUT to listOf(197, 198),
                C.REVERB_HIGH_CUT to listOf(199, 200),
                C.REVERB_HIGH_RATIO to 201,
                C.REVERB_LOW_RATIO to 202,
                C.REVERB_LEVEL to 203,
                C.REVERB_TIME to 193,
                C.REVERB_FILTER to 194,
                C.GATE_SW to 223,
                C.GATE_THRESHOLD to 209,
                C.GATE_RELEASE to 210
        )

        fun writeDump(dump: MutableList<Byte>, id: Int, value: Pair<Byte, Byte>) {
            val cell = dumpMap[id]
            when (cell) {
                is List<*> -> {
                    val listCell = cell as List<Int>
                    dump[listCell[0]] = value.first
                    dump[listCell[1]] = value.second
                }
                is Int -> dump[cell] = value.second
            }
        }

        fun thr5and10(dump: List<Byte>): List<ByteArray> {
            val result = ArrayList<ByteArray>()

            fun add(cmd: ByteArray) {
                result.add(C.SEND_CMD + cmd + C.END.toByte())
            }

            fun add(idCmd: Int, param: Byte) {
                add(C.byteArrayOf(idCmd, 0x00) + param)
            }

            fun add(id: Int, param: Byte, param2: Byte) {
                add(C.byteArrayOf(id) + param + param2)
            }

            if (dump.size != 256) return result

            add(C.AMP, dump[128])
            add(C.K_GAIN, dump[129])
            add(C.K_MASTER, dump[130])
            add(C.K_BASS, dump[131])
            add(C.K_MID, dump[132])
            add(C.K_TREB, dump[133])
            // cab
            add(C.CAB, dump[134])
            // compressor
            add(C.COMPRESSOR_SW, dump[159])
            if (dump[159].toInt() == 0) {
                add(C.COMPRESSOR_STOMP_SUSTAIN, dump[145])
                add(C.COMPRESSOR_STOMP_OUTPUT, dump[146])
                add(C.COMPRESSOR_RACK_THRESHOLD, dump[145], dump[146])
                add(C.COMPRESSOR_RACK_ATTACK, dump[147])
                add(C.COMPRESSOR_RACK_RELEASE, dump[148])
                add(C.COMPRESSOR_RACK_RATIO, dump[149])
                add(C.COMPRESSOR_RACK_KNEE, dump[150])
                add(C.COMPRESSOR_RACK_OUTPUT, dump[151], dump[152])
            }
            // effects
            add(C.EFFECTS_SW, dump[175])
            if (dump[175].toInt() == 0) {
                add(C.EFFECTS_MODE, dump[160])
                add(C.EFFECT_KNOB1, dump[161])
                add(C.EFFECT_KNOB2, dump[162])
                add(C.EFFECT_KNOB3, dump[163])
                add(C.EFFECT_KNOB4, dump[164])
                add(C.EFFECT_KNOB5, dump[165])
            }
            // delay
            add(C.DELAY_SW, dump[191])
            if (dump[191].toInt() == 0) {
                add(C.DELAY_FEEDBACK, dump[179])
                add(C.DELAY_HIGH_CUT, dump[180], dump[181])
                add(C.DELAY_LOW_CUT, dump[182], dump[183])
                add(C.DELAY_LEVEL, dump[184])
                add(C.DELAY_TIME, dump[177], dump[178])
            }

            //reverb
            add(C.REVERB_SW, dump[207])
            if (dump[207].toInt() == 0) {
                add(C.REVERB_MODE, dump[192])
                add(C.REVERB_TIME, dump[193], dump[194])
                add(C.REVERB_PRE_DELAY, dump[195], dump[196])
                add(C.REVERB_LOW_CUT, dump[197], dump[198])
                add(C.REVERB_HIGH_CUT, dump[199], dump[200])
                add(C.REVERB_HIGH_RATIO, dump[201])
                add(C.REVERB_LOW_RATIO, dump[202])
                add(C.REVERB_LEVEL, dump[203])
                if (dump[192].toInt() == 3) {
                    add(C.REVERB_TIME, dump[193])
                    add(C.REVERB_FILTER, dump[194])
                }
            }
            // gate
            add(C.GATE_SW, dump[223])
            if (dump[223].toInt() == 0) {
                add(C.GATE_THRESHOLD, dump[209])
                add(C.GATE_RELEASE, dump[210])
            }
            return result
        }

        fun dumpToData(settings: ByteArray): List<Byte> {
            return settings.slice(IntRange(THR_DUMP_OFFSET, THR_DUMP_SIZE - 3))
        }

        fun dumpTo(settings: ByteArray): List<ByteArray> {
            val result = ArrayList<ByteArray>()

            if (settings.size == THR_DUMP_SIZE) {
                val dump = settings.slice(IntRange(THR_DUMP_OFFSET, THR_DUMP_SIZE - 3))
                result.addAll(thr5and10(dump))
            }
            return result
        }

    }
}

/**
 * data dump
 */
class YdlPreset(val data: List<Byte>) {
    var name: String = ""

    init {

    }
}