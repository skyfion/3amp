package xyz.lazysoft.a3amp.amp

class Constants {
    companion object {
        fun byteArrayOf(vararg a: Int): ByteArray {
            return if (a.isNotEmpty())
                a.map { i -> i.toByte() }.toByteArray()
            else
                ByteArray(0)
        }

        const val THR_DATA_SIZE = 276

        const val ON = 0x00
        const val OFF = 0x7F

        val HEAD = byteArrayOf(0xF0, 0x43, 0x7D)
        val END = byteArrayOf(0xF7)
        val SEND_CMD = HEAD + byteArrayOf(0x10, 0x41, 0x30, 0x01)
        val REQ_SETTINGS = HEAD + byteArrayOf(0x20, 0x44, 0x54, 0x41, 0x31, 0x41, 0x6c, 0x6c, 0x50) + END
        val HEART_BEAT = HEAD + byteArrayOf(0x60, 0x44, 0x54, 0x41)
        // CMD ID
        // knobs id
        const val K_GAIN = 0x01
        const val K_MASTER = 0x02
        const val K_BASS = 0x03
        const val K_MID = 0x04
        const val K_TREB = 0x05
        const val CAB = 0x06 // range 0x00 - 0x05
        const val AMP = 0x00
        const val COMPRESSOR_SW = 0x1F // 00 - on, 7f - off
        const val COMPRESSOR_MODE = 0x10
        const val COMPRESSOR_STOMP_SUSTAIN = 0x11
        const val COMPRESSOR_STOMP_OUTPUT = 0x12
        const val COMPRESSOR_RACK_THRESHOLD = 0x11
        const val COMPRESSOR_RACK_ATTACK = 0x13
        const val COMPRESSOR_RACK_RELEASE = 0x14
        const val COMPRESSOR_RACK_OUTPUT = 0x17
        const val COMPRESSOR_RACK_RATIO = 0x15  // 0 - 5
        const val COMPRESSOR_RACK_KNEE = 0x16  // 0 - 2

        const val EFFECTS_SW = 0x2f
        const val EFFECTS_MODE = 0x20 // chorus flanger tremolo phaser
        const val EFFECT_KNOB1 = 0x21
        const val EFFECT_KNOB2 = 0x22
        const val EFFECT_KNOB3 = 0x23
        const val EFFECT_KNOB4 = 0x24
        const val EFFECT_KNOB5 = 0x25

        const val DELAY_SW = 0x3f
        const val DELAY_FEEDBACK = 0x33
        const val DELAY_LEVEL = 0x38
        const val DELAY_TIME = 0x31
        const val DELAY_HIGH_CUT = 0x34
        const val DELAY_LOW_CUT = 0x36

        const val GATE_SW = 0x5f
        const val GATE_RELEASE = 0x52
        const val GATE_THRESHOLD = 0x51
        const val REVERB_SW = 0x4f
        const val REVERB_MODE = 0x40
        const val REVERB_TIME = 0x41
        const val REVERB_FILTER = 0x42
        const val REVERB_PRE_DELAY = 0x43
        const val REVERB_LOW_CUT = 0x45
        const val REVERB_HIGH_CUT = 0x47
        const val REVERB_HIGH_RATIO = 0x49
        const val REVERB_LOW_RATIO = 0x4a
        const val REVERB_LEVEL = 0x4b

        // light
        val LIGHT_ON = HEAD + byteArrayOf(0x30, 0x41, 0x30, 0x01, 0x01) + END
        val LIGHT_OFF = HEAD + byteArrayOf(0x30, 0x41, 0x30, 0x01, 0x00) + END


    }
}

