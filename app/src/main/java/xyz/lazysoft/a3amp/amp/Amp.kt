package xyz.lazysoft.a3amp.amp

import xyz.lazysoft.a3amp.amp.Constants.Companion.END
import xyz.lazysoft.a3amp.amp.Constants.Companion.HEART_BEAT
import xyz.lazysoft.a3amp.amp.Constants.Companion.OFF
import xyz.lazysoft.a3amp.amp.Constants.Companion.ON
import xyz.lazysoft.a3amp.amp.Constants.Companion.REQ_SETTINGS
import xyz.lazysoft.a3amp.amp.Constants.Companion.SEND_CMD
import xyz.lazysoft.a3amp.amp.Constants.Companion.TAG
import xyz.lazysoft.a3amp.amp.Constants.Companion.THR_DATA_SIZE
import xyz.lazysoft.a3amp.amp.Utils.byteArrayOf
import xyz.lazysoft.a3amp.amp.Utils.intToParam
import xyz.lazysoft.a3amp.amp.Utils.paramToInt
import xyz.lazysoft.a3amp.components.AmpComponent
import xyz.lazysoft.a3amp.midi.SysExMidiManager
import xyz.lazysoft.a3amp.persistence.AmpPreset
import java.util.logging.Logger
import kotlin.properties.Delegates


enum class AmpModel(val model: Byte) {
    THR5(0x30),
    THR10(0x31),
    THR10X(0x32),
    THR10C(0x33),
    THR5A(0x34)
}

class Amp(val midiManager: SysExMidiManager) {
    private val logger = Logger.getLogger(TAG)

    private var ampModel: AmpModel? by Delegates.observable<AmpModel?>(null)
    { _, old, new ->
        if (old != new) midiManager.sendSysExCmd(REQ_SETTINGS)
    }

    var modelAmpDetect: ((AmpModel) -> Unit)? = null

    private var requestCallBack: ((ByteArray) -> Unit)? = null

    val dumpState: PresetDumpState = PresetDumpState(Constants.initPresetDump.toByteArray())

    private var heartBeat: ByteArray? by Delegates.observable<ByteArray?>(null)
    { _, oldValue, newValue ->

        if ((oldValue == null && newValue != null) || (newValue != null && oldValue != null && !newValue.contentEquals(oldValue))) {
            val model = AmpModel.values().firstOrNull { it.model == newValue[7] }
            if (model != null)
                modelAmpDetect?.invoke(model)
            ampModel = model
        }
    }

    init {
        midiManager.sysExtListeners.add { data: ByteArray? ->
            if (data != null) {
                val sig = data.slice(IntRange(0, 6)).toByteArray()
                if (sig.contentEquals(HEART_BEAT)) {
                    heartBeat = data
                } else if (data.size == THR_DATA_SIZE) { // config from amp
                    requestCallBack?.invoke(data)
                    requestCallBack = null
                    // chkeck header
                    // todo chk checksum
                    applyDump(data)
                }
            }
        }


        midiManager.sendSysExtListeners.add {
            if (it != null && it.size > 9) {
                val cmd = it.slice(IntRange(0, 6)).toByteArray()
                if (cmd.contentEquals(Constants.SEND_CMD)) {
                    dumpState.writeDump(it[7].toInt(), Pair(it[8], it[9]))
                }
            }
        }

    }

    fun getCurrrentDump(f: (ByteArray) -> Unit) {
        requestCallBack = f
        midiManager.sendSysExCmd(REQ_SETTINGS)
    }

    fun open() {
        // init empty dump
        YdlDataConverter.thr5and10(dumpState.dump.toList()).forEach { cmd ->
            midiManager.onMidiSystemExclusive(cmd)
        }

    }

    private fun applyDump(dump: ByteArray) {
        YdlDataConverter.dumpTo(dump)
                .forEach { midiManager.onMidiSystemExclusive(it) }
    }


    /**
     * Add common knob
     */
    fun addKnob(knob: AmpComponent<Int>, id: Int): Amp {
        val sendCmd = Constants.SEND_CMD + id.toByte()
        midiManager.sysExtListeners.add {
            if (it != null && it.size > 9) {
                val cmd = it.slice(IntRange(0, 7)).toByteArray()
                if (cmd.contentEquals(sendCmd)) {
                    knob.state = paramToInt(it.sliceArray(IntRange(8, 9)))
                }
            }
        }
        knob.setOnStateChanged {
            //   logger.info("recive ${it} -> ${intToParam(it).joinToString()}")
            midiManager.sendSysExCmd(sendCmd + intToParam(it) + END.toByte())
        }
        return this
    }

    /**
     * Switch on\off block effect
     */
    fun addSwitch(sw: AmpComponent<Boolean>, id: ByteArray): Amp {
        val cmdId = SEND_CMD + id + 0x00
        midiManager.sysExtListeners.add {
            if (it != null && it.size > 9) {
                val cmd = it.slice(IntRange(0, 8)).toByteArray()
                if (cmd.contentEquals(cmdId)) {
                    when (it[9].toInt()) {
                        ON -> sw.state = true
                        OFF -> sw.state = false
                    }
                }
            }
        }
        sw.setOnStateChanged {
            val mode = if (it) ON else OFF
            midiManager.sendSysExCmd(cmdId + mode.toByte() + END.toByte())
        }
        return this
    }


    fun addSpinner(spinner: AmpComponent<Int>, id: Int): Amp {
        val cmdId = SEND_CMD + id.toByte() + 0x00
        midiManager.sysExtListeners.add {
            if (it != null && it.size > 9) {
                val cmd = it.slice(IntRange(0, 8)).toByteArray()
                if (cmd.contentEquals(cmdId)) {
                    spinner.state = it[9].toInt()
                }
            }
        }
        spinner.setOnStateChanged {
            midiManager.sendSysExCmd(cmdId + it.toByte() + END.toByte())
        }
        return this
    }

    fun addOffSpinner(spinner: AmpComponent<Int>, id: Int, swId: Int): Amp {
        val swCmdId = SEND_CMD + swId.toByte() + 0x00
        val cmdId = SEND_CMD + id.toByte() + 0x00
        midiManager.sysExtListeners.add {
            if (it != null && it.size > 9) {
                val cmd = it.slice(IntRange(0, 8)).toByteArray()
                if (cmd.contentEquals(swCmdId) && it[9] == OFF.toByte()) {
                    spinner.state = 0
                } else if (cmd.contentEquals(cmdId)) {
                    spinner.state = it[9].toInt() + 1
                }
            }
        }
        spinner.setOnStateChanged {
            if (it == 0)
                midiManager.sendSysExCmd(SEND_CMD + byteArrayOf(swId, 0x00, OFF, END))
            else {
                midiManager.sendSysExCmd(SEND_CMD + byteArrayOf(swId, 0x00, ON, END))
                midiManager.sendSysExCmd(SEND_CMD + byteArrayOf(id, 0x00, (it - 1), END))
            }
        }
        return this
    }

    fun addSwSpinner(carousel: AmpComponent<Int>, swId: Int): Amp {
        val cmdId = SEND_CMD + swId.toByte() + 0x00
        midiManager.sysExtListeners.add {
            if (it != null && it.size > 9) {
                val cmd = it.slice(IntRange(0, 8)).toByteArray()
                if (cmd.contentEquals(cmdId)) {
                    carousel.state = if (it[9] == OFF.toByte()) 0 else 1
                }
            }
        }
        carousel.setOnStateChanged {
            val value = if (it == 0) OFF else ON
            midiManager.sendSysExCmd(cmdId + value.toByte() + END.toByte())
        }
        return this
    }

    var selectPreset: AmpPreset? = null
        set(value) {
            value?.let { loadPreset(it) }
            field = value
        }

    private fun loadPreset(preset: AmpPreset) {
        preset.dump.let {
            //  val cmd = Constants.HEAD + Constants.DUMP_PREFIX + 0x31 + it + Constants.DUMP_POSTFIX + 0x1F + Constants.END
            //todo need unit test
            // logger.info("load dump -> " + it.joinToString())
            YdlDataConverter.thr5and10(it.toList()).forEach { cmd ->
                midiManager.onMidiSystemExclusive(cmd)
            }
        }
    }
}

