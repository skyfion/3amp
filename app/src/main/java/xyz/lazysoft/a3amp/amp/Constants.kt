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
        val DUMP_PREFIX = byteArrayOf(0x00, 0x02, 0x0c, 0x44, 0x54, 0x41)
        val DUMP_POSTFIX = byteArrayOf(0x41, 0x6c, 0x6c, 0x50, 0x00, 0x00, 0x7f, 0x7f)
        val END = 0xF7
        val SEND_CMD = HEAD + byteArrayOf(0x10, 0x41, 0x30, 0x01)
        val REQ_SETTINGS = HEAD + byteArrayOf(0x20, 0x44, 0x54, 0x41, 0x31, 0x41, 0x6c, 0x6c, 0x50, END)
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
        const val REVERB_SPRING_FILTER = 0x42
        const val REVERB_PRE_DELAY = 0x43
        const val REVERB_LOW_CUT = 0x45
        const val REVERB_HIGH_CUT = 0x47
        const val REVERB_HIGH_RATIO = 0x49
        const val REVERB_LOW_RATIO = 0x4a
        const val REVERB_LEVEL = 0x4b

        // light
        val LIGHT_ON = HEAD + byteArrayOf(0x30, 0x41, 0x30, 0x01, 0x01, END)
        val LIGHT_OFF = HEAD + byteArrayOf(0x30, 0x41, 0x30, 0x01, 0x00, END)
        val TAG = "3amp"

        val DUMP_MAP = hashMapOf(
                this.AMP to 128,
                this.K_GAIN to 129,
                this.K_MASTER to 130,
                this.K_BASS to 131,
                this.K_MID to 132,
                this.K_TREB to 133,
                this.CAB to 134,
                this.COMPRESSOR_SW to 159,
                this.COMPRESSOR_STOMP_SUSTAIN to 145,
                this.COMPRESSOR_STOMP_OUTPUT to 146,
                this.COMPRESSOR_RACK_THRESHOLD to listOf(145, 146),
                this.COMPRESSOR_RACK_ATTACK to 147,
                this.COMPRESSOR_RACK_RELEASE to 148,
                this.COMPRESSOR_RACK_RATIO to 149,
                this.COMPRESSOR_RACK_KNEE to 150,
                this.COMPRESSOR_RACK_OUTPUT to listOf(151, 152),
                this.EFFECTS_SW to 175,
                this.EFFECTS_MODE to 160,
                this.EFFECT_KNOB1 to 161,
                this.EFFECT_KNOB2 to 162,
                this.EFFECT_KNOB3 to 163,
                this.EFFECT_KNOB4 to 164,
                this.EFFECT_KNOB5 to 165,
                this.DELAY_TIME to listOf(177, 178),
                this.DELAY_FEEDBACK to 179,
                this.DELAY_HIGH_CUT to listOf(180, 181),
                this.DELAY_LOW_CUT to listOf(182, 183),
                this.DELAY_LEVEL to 184,
                this.DELAY_SW to 191,
                this.REVERB_SW to 207,
                this.REVERB_MODE to 192,
                this.REVERB_TIME to 194,
                this.REVERB_PRE_DELAY to listOf(195, 196),
                this.REVERB_LOW_CUT to listOf(197, 198),
                this.REVERB_HIGH_CUT to listOf(199, 200),
                this.REVERB_HIGH_RATIO to 201,
                this.REVERB_LOW_RATIO to 202,
                this.REVERB_LEVEL to 203,
                this.REVERB_TIME to 193,
                this.REVERB_SPRING_FILTER to 194,
                this.GATE_SW to 223,
                this.GATE_THRESHOLD to 209,
                this.GATE_RELEASE to 210
        )

    }
}

