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
            if (settings.size == THR_DUMP_SIZE) {
                val dump = settings.slice(IntRange(THR_DUMP_OFFSET, THR_DUMP_SIZE - 3))
                result.add(Amp.K_GAIN + dump[129] + Amp.END)
                result.add(Amp.K_MASTER + dump[130] + Amp.END)
                result.add(Amp.K_BASS + dump[131] + Amp.END)
                result.add(Amp.K_MID + dump[132] + Amp.END)
                result.add(Amp.K_TREB + dump[133] + Amp.END)
            }
            return result
        }
    }
}