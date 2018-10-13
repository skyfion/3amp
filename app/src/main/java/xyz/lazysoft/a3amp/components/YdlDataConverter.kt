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
            val add = { cmd: ByteArray -> result.add(cmd + Amp.END) }
            if (settings.size == THR_DUMP_SIZE) {
                val dump = settings.slice(IntRange(THR_DUMP_OFFSET, THR_DUMP_SIZE - 3))
                add(Amp.AMP + dump[128])
                add(Amp.K_GAIN + 0x00 + dump[129])
                add(Amp.K_MASTER + 0x00 + dump[130])
                add(Amp.K_BASS + 0x00 + dump[131])
                add(Amp.K_MID + 0x00 + dump[132])
                add(Amp.K_TREB + 0x00 + dump[133])
                // cab
                add(Amp.CAB + dump[134])
                // compressor
                add(Amp.COMPRESSOR_SW + dump[159])
                add(Amp.COMPRESSOR_STOMP_SUSTAIN + 0x00 + dump[145])
                add(Amp.COMPRESSOR_STOMP_OUTPUT + 0x00 + dump[146])
                add(Amp.COMPRESSOR_RACK_THRESHOLD + dump[145] + dump[146])
                add(Amp.COMPRESSOR_RACK_ATTACK + 0x00 + dump[147])
                add(Amp.COMPRESSOR_RACK_RELEASE + 0x00 + dump[148])
                add(Amp.COMPRESSOR_RACK_RATIO + 0x00 + dump[149])
                add(Amp.COMPRESSOR_RACK_KNEE + 0x00 + dump[150])
                add(Amp.COMPRESSOR_RACK_OUTPUT + dump[151] + dump[152])

                // effects
                add(Amp.EFFECTS_SW + dump[175])
                add(Amp.EFFECTS_MODE + dump[160])
                add(Amp.EFFECT_KNOB1 + 0x00 + dump[161])
                add(Amp.EFFECT_KNOB2 + 0x00 + dump[162])
                add(Amp.EFFECT_KNOB3 + 0x00 + dump[163])
                add(Amp.EFFECT_KNOB4 + 0x00 + dump[164])
                add(Amp.EFFECT_KNOB5 + 0x00 + dump[165])

                // delay
                add(Amp.DELAY_SW + dump[191])
                add(Amp.DELAY_FEEDBACK + 0x00 + dump[179])
                add(Amp.DELAY_HIGH_CUT + dump[180] + dump[181])
                add(Amp.DELAY_LOW_CUT + dump[182] + dump[183])
                add(Amp.DELAY_LEVEL + 0x00 + dump[184])

                // gate
                add(Amp.GATE_SW + dump[223])
                add(Amp.GATE_THRESHOLD + 0x00 + dump[209])
                add(Amp.GATE_RELEASE + 0x00 + dump[210])
            }
            return result
        }
    }
}