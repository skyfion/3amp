package xyz.lazysoft.a3amp.amp

import xyz.lazysoft.a3amp.amp.Constants.Companion as Const

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

        private fun thr5and10(dump: List<Byte>): List<ByteArray> {
            val result = ArrayList<ByteArray>()

            fun add(cmd: ByteArray) {
                result.add(Const.SEND_CMD + cmd + Const.END)
            }

            fun add(idCmd: Int, param: Byte) {
                add(Const.byteArrayOf(idCmd, 0x00) + param)
            }

            fun add(id: Int, param: Byte, param2: Byte) {
                add(Const.byteArrayOf(id) + param + param2)
            }

            if (dump.size != 256) return result

            add(Const.AMP, dump[128])
            add(Const.K_GAIN, dump[129])
            add(Const.K_MASTER, dump[130])
            add(Const.K_BASS, dump[131])
            add(Const.K_MID, dump[132])
            add(Const.K_TREB, dump[133])
            // cab
            add(Const.CAB, dump[134])
            // compressor
            add(Const.COMPRESSOR_SW, dump[159])
            if (dump[159].toInt() != 0) {
                add(Const.COMPRESSOR_STOMP_SUSTAIN, dump[145])
                add(Const.COMPRESSOR_STOMP_OUTPUT, dump[146])
                add(Const.COMPRESSOR_RACK_THRESHOLD, dump[145], dump[146])
                add(Const.COMPRESSOR_RACK_ATTACK, dump[147])
                add(Const.COMPRESSOR_RACK_RELEASE, dump[148])
                add(Const.COMPRESSOR_RACK_RATIO, dump[149])
                add(Const.COMPRESSOR_RACK_KNEE, dump[150])
                add(Const.COMPRESSOR_RACK_OUTPUT, dump[151], dump[152])
            }
            // effects
            add(Const.EFFECTS_SW, dump[175])
            if (dump[175].toInt() != 0) {
                add(Const.EFFECTS_MODE, dump[160])
                add(Const.EFFECT_KNOB1, dump[161])
                add(Const.EFFECT_KNOB2, dump[162])
                add(Const.EFFECT_KNOB3, dump[163])
                add(Const.EFFECT_KNOB4, dump[164])
                add(Const.EFFECT_KNOB5, dump[165])
            }
            // delay
            add(Const.DELAY_SW, dump[191])
            if (dump[191].toInt() != 0) {
                add(Const.DELAY_FEEDBACK, dump[179])
                add(Const.DELAY_HIGH_CUT, dump[180], dump[181])
                add(Const.DELAY_LOW_CUT, dump[182], dump[183])
                add(Const.DELAY_LEVEL, dump[184])
            }
            //reverb
            add(Const.REVERB_SW, dump[207])
            if (dump[207].toInt() != 0) {
                add(Const.REVERB_MODE, dump[192])
                add(Const.REVERB_TIME, dump[193], dump[194])
                add(Const.REVERB_PRE_DELAY, dump[195], dump[196])
                add(Const.REVERB_LOW_CUT, dump[197], dump[198])
                add(Const.REVERB_HIGH_CUT, dump[199], dump[200])
                add(Const.REVERB_HIGH_RATIO, dump[201])
                add(Const.REVERB_LOW_RATIO, dump[202])
                add(Const.REVERB_LEVEL, dump[203])
                if (dump[192].toInt() == 3) {
                    add(Const.REVERB_TIME, dump[193])
                    add(Const.REVERB_FILTER, dump[194])
                }
            }
            // gate
            add(Const.GATE_SW, dump[223])
            if (dump[223].toInt() != 0) {
                add(Const.GATE_THRESHOLD, dump[209])
                add(Const.GATE_RELEASE, dump[210])
            }
            return result
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