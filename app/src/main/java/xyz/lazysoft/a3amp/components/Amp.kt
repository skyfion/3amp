package xyz.lazysoft.a3amp.components

import xyz.lazysoft.a3amp.UsbMidiManager
import java.util.logging.Logger
import kotlin.properties.Delegates

enum class AmpModel(val model: Byte) {
    THR5(0x30),
    THR10(0x31),
    THR10X(0x32),
    THR10C(0x33),
    THR5A(0x34)
}

class Amp(private val midiManager: UsbMidiManager) {
    val logger = Logger.getLogger(this.javaClass.name)
    private val knobs: MutableList<AmpKnob> = mutableListOf()
    private val switchers: MutableList<AmpSwitch> = mutableListOf()
    private var ampModel: AmpModel? by Delegates.observable<AmpModel?>(null) {
        _, old, new->
                if (old != new) midiManager.sendSysExCmd(REQ_SETTINGS)
    }
    var modelAmpDetect: ((String) -> Unit)? = null

    init {
        midiManager.sysExtListeners.add { data: ByteArray? ->
            if (data != null) {
                val sig = data.slice(IntRange(0, 6)).toByteArray()
                if (sig.contentEquals(HEART_BEAT)) {
                    val model = AmpModel.values().firstOrNull { it.model == data[7] }
                    if (model != null)
                        modelAmpDetect?.invoke(model.name)
                    ampModel = model
                } else if (data.size == THR_DATA_SIZE) {
                    // chkeck header
                    // todo chk checksum
                    YdlDataConverter.binDataToCmdList(data)
                            .forEach{ midiManager.onMidiSystemExclusive(it) }
                }
            }
        }

    }


    /**
     * Add common knob
     * @param knob interface AmpKnob
     * @param id is 9 byte start cmd
     */
    fun addKnob(knob: AmpKnob, id: ByteArray): Amp {
        knobs.add(knob)
        logger.info("add id "+ id.joinToString())
        midiManager.sysExtListeners.add {
            if (it != null && it.size > 9) {
                logger.info("it " + it.joinToString())

                val cmd = it.slice(IntRange(0, 8)).toByteArray()
                if (cmd.contentEquals(id)) {
                    knob.state = it[9].toInt()
                }
            }
        }
        knob.setOnStateChanged {
            midiManager.sendSysExCmd( id + it.toByte() + END)
        }
        return this
    }

    /**
     * Switch on\off block effect
     */
    fun addSwitch(sw: AmpSwitch, id: ByteArray) {
        switchers.add(sw)
        midiManager.sysExtListeners.add {
            if (it != null && it.size > 9) {
                val cmd = it.slice(IntRange(0, 8)).toByteArray()
                if (cmd.contentEquals(id)) {
                     when (it[9]) {
                         0x00.toByte() -> sw.isChecked = true
                         0x7f.toByte() -> sw.isChecked = false
                     }
                }
            }
        }
    }

    fun addSpinner(spinner: AmpSpinner, id: ByteArray): Amp {
        midiManager.sysExtListeners.add {
            if (it != null && it.size > 9) {
                val cmd = it.slice(IntRange(0, 8)).toByteArray()
                if (cmd.contentEquals(id)) {
                    spinner.pos = it[9].toInt()
                }
            }
        }
        spinner.setOnSelectItem {
            midiManager.sendSysExCmd(id + it.toByte() + END)
        }
        return this
    }

    companion object {
        private fun byteArrayOf(vararg a: Int): ByteArray {
            return if (a.isNotEmpty())
                a.map { i -> i.toByte() }.toByteArray()
            else
                ByteArray(0)
        }

        val THR_DATA_SIZE = 276

        val HEAD = byteArrayOf(0xF0, 0x43, 0x7D)
        val END = byteArrayOf(0xF7)
        val SEND_CMD = HEAD + byteArrayOf(0x10, 0x41, 0x30, 0x01) // todo 0x30 is id amp ?
        val REQ_SETTINGS = HEAD + byteArrayOf(0x20, 0x44, 0x54, 0x41, 0x31, 0x41, 0x6c, 0x6c, 0x50) + END
        val HEART_BEAT = HEAD + byteArrayOf(0x60, 0x44, 0x54, 0x41)
        // CMD ID
        // knobs id
        val K_GAIN = SEND_CMD + byteArrayOf(0x01, 0x00)
        val K_MASTER = SEND_CMD + byteArrayOf(0x02, 0x00)
        val K_BASS = SEND_CMD + byteArrayOf(0x03, 0x00)
        val K_MID = SEND_CMD + byteArrayOf(0x04, 0x00)
        val K_TREB = SEND_CMD + byteArrayOf(0x05, 0x00)
        val CAB = SEND_CMD + byteArrayOf(0x06, 0x00) // range 0x00 - 0x05
        val AMP = SEND_CMD + byteArrayOf(0x00, 0x00)
        val COMPRESSOR_ON = SEND_CMD + byteArrayOf(0x1F, 0x00) // 00 - on, 7f - off
        val COMPRESSOR_STOMP = SEND_CMD + byteArrayOf(0x10, 0x00, 0x00)
        val COMPRESSOR_STOMP_SUSTAIN = SEND_CMD + byteArrayOf(0x11, 0x00)
        val COMPRESSOR_STOMP_OUTPUT = SEND_CMD + byteArrayOf(0x12, 0x00)
        val COMPRESSOR_RACK = SEND_CMD + byteArrayOf(0x10, 0x00, 0x01)
        val COMPRESSOR_RACK_THRESHOLD = SEND_CMD + byteArrayOf(0x11)
        // light
        val LIGHT_ON = HEAD + byteArrayOf(0x30, 0x41, 0x30, 0x01, 0x01) + END
        val LIGHT_OFF = HEAD + byteArrayOf(0x30, 0x41, 0x30, 0x01, 0x00) + END

    }
}

