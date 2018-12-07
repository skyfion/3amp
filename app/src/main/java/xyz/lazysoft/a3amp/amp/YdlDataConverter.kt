package xyz.lazysoft.a3amp.amp

import xyz.lazysoft.a3amp.amp.Constants.Companion.THR_DATA_SIZE
import xyz.lazysoft.a3amp.amp.Constants.Companion.THR_DUMP_OFFSET
import xyz.lazysoft.a3amp.amp.Constants.Companion as C

/**
 * Yamaha thr ydl file reader/converter
 * */
class YdlDataConverter {
    companion object {

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
                add(C.REVERB_TIME, dump[194])
                add(C.REVERB_PRE_DELAY, dump[195], dump[196])
                add(C.REVERB_LOW_CUT, dump[197], dump[198])
                add(C.REVERB_HIGH_CUT, dump[199], dump[200])
                add(C.REVERB_HIGH_RATIO, dump[201])
                add(C.REVERB_LOW_RATIO, dump[202])
                add(C.REVERB_LEVEL, dump[203])
                if (dump[192].toInt() == 3) {
                    add(C.REVERB_TIME, dump[193])
                    add(C.REVERB_SPRING_FILTER, dump[194])
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

        fun dumpTo(settings: ByteArray): List<ByteArray> {
            val result = ArrayList<ByteArray>()

            if (settings.size == THR_DATA_SIZE) {
                val dump = settings.slice(IntRange(THR_DUMP_OFFSET, THR_DATA_SIZE - 3))
                result.addAll(thr5and10(dump))
            }
            return result
        }

    }
}
