package xyz.lazysoft.a3amp.components

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

        fun binDataToCmdList(settings: ByteArray): List<ByteArray> {
            val result = ArrayList<ByteArray>()
            val add = {cmd: ByteArray -> result.add(cmd + Amp.END)}
            if (settings.size == THR_DUMP_SIZE) {
                val dump = settings.slice(IntRange(THR_DUMP_OFFSET, THR_DUMP_SIZE - 3))
                add(Amp.AMP + dump[128])
                add(Amp.K_GAIN + dump[129])
                add(Amp.K_MASTER + dump[130])
                add(Amp.K_BASS + dump[131])
                add(Amp.K_MID + dump[132])
                add(Amp.K_TREB + dump[133])
                // cab
                add(Amp.CAB + dump[134])
                // compressor
                add(Amp.COMPRESSOR_SW + dump[159])
                add(Amp.COMPRESSOR_STOMP_SUSTAIN + dump[145])
                add(Amp.COMPRESSOR_STOMP_OUTPUT + dump[146])
                // todo

                // effects
                add(Amp.EFFECTS_SW + dump[175])
                add(Amp.EFFECTS_MODE + dump[160])
                add(Amp.EFFECT_KNOB1 + dump[161])
                add(Amp.EFFECT_KNOB2 + dump[162])
                add(Amp.EFFECT_KNOB3 + dump[163])
                add(Amp.EFFECT_KNOB4 + dump[164])
                add(Amp.EFFECT_KNOB5 + dump[165])

                // delay
                add(Amp.DELAY_SW + dump[191])

                // gate
                add(Amp.GATE_SW + dump[223])
                add(Amp.GATE_THRESHOLD + dump[209])
                add(Amp.GATE_RELEASE + dump[210])
            }
            return result
        }
    }
}