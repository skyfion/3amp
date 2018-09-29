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
                add(Amp.COMPRESSOR_ON + dump[159])
            }
            return result
        }
    }
}