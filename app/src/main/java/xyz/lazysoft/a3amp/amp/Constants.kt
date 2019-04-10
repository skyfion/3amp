package xyz.lazysoft.a3amp.amp

import xyz.lazysoft.a3amp.amp.Utils.byteArrayOf

class Constants {
    companion object {

        const val THR_DATA_SIZE = 276
        const val THR_DUMP_OFFSET = 18
        const val THR_SYSEX_SIZE = 256
        const val THR_SYSEX_SHIFT = 128

        const val YDL_FILE_SIZE = 26108
        const val YDL_PRESET_COUNT = 100
        const val YDL_PRESET_BODY_SIZE = 261
        const val YDL_HEAD = 8
        const val YDL_HEADER_TEXT = "DTAB"
        const val YDL_NAME = 64
        const val YDL_DATA = 192
        const val YDL_DATA_SHIFT = 69

        const val ON = 0x00
        const val OFF = 0x7F

        val HEAD = byteArrayOf(0xF0, 0x43, 0x7D)
        val DUMP_PREFIX = byteArrayOf(0x00, 0x02, 0x0c, 0x44, 0x54, 0x41)
        val DUMP_POSTFIX = byteArrayOf(0x41, 0x6c, 0x6c, 0x50, 0x00, 0x00, 0x7f, 0x7f)
        const val END = 0xF7
        val SEND_CMD = HEAD + byteArrayOf(0x10, 0x41, 0x30, 0x01)
        val REQ_SETTINGS = HEAD + byteArrayOf(0x20, 0x44, 0x54, 0x41, 0x31, 0x41, 0x6c, 0x6c, 0x50, END)
        val HEART_BEAT = HEAD + byteArrayOf(0x60, 0x44, 0x54, 0x41)

        const val AMP = 0x00
        const val K_GAIN = 0x01
        const val K_MASTER = 0x02
        const val K_BASS = 0x03
        const val K_MID = 0x04
        const val K_TREB = 0x05
        const val CAB = 0x06 // range 0x00 - 0x05
        const val COMPRESSOR_SW = 0x1F // 00 - on, 7f - off
        const val COMPRESSOR_MODE = 0x10
        const val COMPRESSOR_STOMP_SUSTAIN = 0x11
        const val COMPRESSOR_RACK_THRESHOLD = 0x11
        const val COMPRESSOR_STOMP_OUTPUT = 0x12
        const val COMPRESSOR_RACK_ATTACK = 0x13
        const val COMPRESSOR_RACK_RELEASE = 0x14
        const val COMPRESSOR_RACK_RATIO = 0x15  // 0 - 5
        const val COMPRESSOR_RACK_KNEE = 0x16  // 0 - 2
        const val COMPRESSOR_RACK_OUTPUT = 0x17
        const val EFFECTS_SW = 0x2f
        const val EFFECTS_MODE = 0x20 // chorus flanger tremolo phaser
        const val EFFECT_KNOB1 = 0x21
        const val EFFECT_KNOB2 = 0x22
        const val EFFECT_KNOB3 = 0x23
        const val EFFECT_KNOB4 = 0x24
        const val EFFECT_KNOB5 = 0x25
        const val DELAY_SW = 0x3f
        const val DELAY_TIME = 0x31
        const val DELAY_FEEDBACK = 0x33
        const val DELAY_HIGH_CUT = 0x34
        const val DELAY_LOW_CUT = 0x36
        const val DELAY_LEVEL = 0x38
        const val GATE_SW = 0x5f
        const val GATE_THRESHOLD = 0x51
        const val GATE_RELEASE = 0x52
        const val REVERB_SW = 0x4f
        const val REVERB_MODE = 0x40
        const val REVERB_TIME = 0x41
        const val REVERB_SPRING_FILTER = 0x42
        const val REVERB_PRE_DELAY = 0x43
        // may by spring reverb mast be 0x44
        const val REVERB_LOW_CUT = 0x45
        const val REVERB_HIGH_CUT = 0x47
        const val REVERB_HIGH_RATIO = 0x49
        const val REVERB_LOW_RATIO = 0x4a
        const val REVERB_LEVEL = 0x4b

        // light
        val LIGHT_ON = HEAD + byteArrayOf(0x30, 0x41, 0x30, 0x01, 0x01, END)
        val LIGHT_OFF = HEAD + byteArrayOf(0x30, 0x41, 0x30, 0x01, 0x00, END)
        val TAG = "3amp"

        val TWO_BYTES = listOf(
                COMPRESSOR_RACK_OUTPUT,
                DELAY_TIME,
                DELAY_HIGH_CUT,
                DELAY_LOW_CUT,
                REVERB_PRE_DELAY,
                REVERB_LOW_CUT,
                REVERB_HIGH_CUT)

        val DUMP_MAP = hashMapOf(
                AMP to 128,
                K_GAIN to 129,
                K_MASTER to 130,
                K_BASS to 131,
                K_MID to 132,
                K_TREB to 133,
                CAB to 134,
                COMPRESSOR_SW to 159,
                COMPRESSOR_MODE to 144,
                COMPRESSOR_STOMP_SUSTAIN to 145,
                COMPRESSOR_STOMP_OUTPUT to 146,
                // COMPRESSOR_RACK_THRESHOLD to listOf(145, 146),
                COMPRESSOR_RACK_ATTACK to 147,
                COMPRESSOR_RACK_RELEASE to 148,
                COMPRESSOR_RACK_RATIO to 149,
                COMPRESSOR_RACK_KNEE to 150,
                COMPRESSOR_RACK_OUTPUT to listOf(151, 152),
                EFFECTS_SW to 175,
                EFFECTS_MODE to 160,
                EFFECT_KNOB1 to 161,
                EFFECT_KNOB2 to 162,
                EFFECT_KNOB3 to 163,
                EFFECT_KNOB4 to 164,
                EFFECT_KNOB5 to 165,
                DELAY_TIME to listOf(177, 178),
                DELAY_FEEDBACK to 179,
                DELAY_HIGH_CUT to listOf(180, 181),
                DELAY_LOW_CUT to listOf(182, 183),
                DELAY_LEVEL to 184,
                DELAY_SW to 191,
                REVERB_SW to 207,
                REVERB_MODE to 192,
                REVERB_TIME to 194,
                REVERB_PRE_DELAY to listOf(195, 196),
                REVERB_LOW_CUT to listOf(197, 198),
                REVERB_HIGH_CUT to listOf(199, 200),
                REVERB_HIGH_RATIO to 201,
                REVERB_LOW_RATIO to 202,
                REVERB_LEVEL to 203,
                //  this.REVERB_SPRING_REVERB to 193,
                REVERB_SPRING_FILTER to 194,
                GATE_SW to 223,
                GATE_THRESHOLD to 209,
                GATE_RELEASE to 210
        )

        // 256
        val initPresetDump = arrayOf(
                0x49, 0x6E, 0x69, 0x74, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x32, 0x32, 0x32, 0x32, 0x32, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x32, 0x32, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7F,
                0x00, 0x35, 0x1D, 0x22, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7F,
                0x00, 0x1E, 0x46, 0x2E, 0x5D, 0x60, 0x00, 0x15, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7F,
                0x02, 0x00, 0x1B, 0x00, 0x01, 0x00, 0x61, 0x17, 0x2F, 0x04, 0x09, 0x49, 0x00, 0x00, 0x00, 0x7F,
                0x00, 0x32, 0x32, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7F,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00).map { it.toByte() }

        const val CAROUSEL_TEXT_SIZE: Int = 10
        const val READ_REQUEST_CODE: Int = 42
    }
}

