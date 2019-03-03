package xyz.lazysoft.a3amp.amp

import xyz.lazysoft.a3amp.amp.Constants.Companion.THR_DATA_SIZE
import xyz.lazysoft.a3amp.amp.Constants.Companion.THR_DUMP_OFFSET
import xyz.lazysoft.a3amp.amp.Constants.Companion as C
import xyz.lazysoft.a3amp.amp.Utils.byteArrayOf
import java.util.*

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
                add(byteArrayOf(idCmd, 0x00) + param)
            }

            fun add(id: Int, param: Byte, param2: Byte) {
                add(byteArrayOf(id) + param + param2)
            }

            fun add(ids: List<Int>, data: List<Byte>) {
                ids.forEach { id ->
                    add(id, data[id])
                }
            }

            val data = when (dump.size) {
                Constants.THR_SYSEX_SIZE -> dump.drop(Constants.THR_SYSEX_SHIFT)
                Constants.YDL_DATA -> dump.drop(Constants.YDL_DATA_SHIFT)
                else -> return result
            }

            add(Arrays.asList(
                    C.AMP, C.K_GAIN, C.K_MASTER, C.K_BASS,
                    C.K_MID, C.K_TREB, C.CAB, C.COMPRESSOR_SW, C.EFFECTS_SW, C.DELAY_SW,
                    C.REVERB_SW, C.GATE_SW), data)

            if (data[C.COMPRESSOR_SW].toInt() == 0) {
                add(C.COMPRESSOR_MODE, data[C.COMPRESSOR_MODE])
                if (data[C.COMPRESSOR_MODE].toInt() == 0) {
                    add(Arrays.asList(C.COMPRESSOR_STOMP_SUSTAIN, C.COMPRESSOR_STOMP_OUTPUT), data)
                } else {
                    add(C.COMPRESSOR_RACK_THRESHOLD,
                            data[C.COMPRESSOR_RACK_THRESHOLD],
                            data[C.COMPRESSOR_RACK_THRESHOLD + 1])
                    add(C.COMPRESSOR_RACK_OUTPUT,
                            data[C.COMPRESSOR_RACK_OUTPUT],
                            data[C.COMPRESSOR_RACK_OUTPUT + 1])
                    add(Arrays.asList(
                            C.COMPRESSOR_RACK_ATTACK,
                            C.COMPRESSOR_RACK_RELEASE,
                            C.COMPRESSOR_RACK_RATIO,
                            C.COMPRESSOR_RACK_KNEE), data)
                }
            }
            // effects
            if (data[C.EFFECTS_SW].toInt() == 0) {
                add(Arrays.asList(
                        C.EFFECTS_MODE,
                        C.EFFECT_KNOB1,
                        C.EFFECT_KNOB2,
                        C.EFFECT_KNOB3,
                        C.EFFECT_KNOB4,
                        C.EFFECT_KNOB5), data)
            }
            // delay
            if (data[C.DELAY_SW].toInt() == 0) {
                add(Arrays.asList(C.DELAY_FEEDBACK, C.DELAY_LEVEL), data)
                add(C.DELAY_HIGH_CUT, data[C.DELAY_HIGH_CUT], data[C.DELAY_HIGH_CUT + 1])
                add(C.DELAY_LOW_CUT, data[C.DELAY_LOW_CUT], data[C.DELAY_LOW_CUT + 1])
                add(C.DELAY_TIME, data[C.DELAY_TIME], data[C.DELAY_TIME + 1])
            }

            //reverb
            if (data[C.REVERB_SW].toInt() == 0) {
                add(Arrays.asList(
                        C.REVERB_MODE,
                        C.REVERB_TIME,
                        C.REVERB_HIGH_RATIO,
                        C.REVERB_LOW_RATIO,
                        C.REVERB_LEVEL), data)
                add(C.REVERB_PRE_DELAY, data[C.REVERB_PRE_DELAY], data[C.REVERB_PRE_DELAY + 1])
                add(C.REVERB_LOW_CUT, data[C.REVERB_LOW_CUT], data[C.REVERB_LOW_CUT + 1])
                add(C.REVERB_HIGH_CUT, data[C.REVERB_HIGH_CUT], data[C.REVERB_HIGH_CUT + 1])
                if (data[C.REVERB_MODE].toInt() == 3) {
                    add(Arrays.asList(
                            C.REVERB_TIME,
                            C.REVERB_SPRING_FILTER), data)
                }
            }
            // gate
            if (data[C.GATE_SW].toInt() == 0) {
                add(Arrays.asList(
                        C.GATE_THRESHOLD,
                        C.GATE_RELEASE), data)
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
