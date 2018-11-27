package xyz.lazysoft.a3amp.amp

import xyz.lazysoft.a3amp.amp.Constants.Companion.END
import xyz.lazysoft.a3amp.amp.Constants.Companion.HEART_BEAT
import xyz.lazysoft.a3amp.amp.Constants.Companion.OFF
import xyz.lazysoft.a3amp.amp.Constants.Companion.ON
import xyz.lazysoft.a3amp.amp.Constants.Companion.REQ_SETTINGS
import xyz.lazysoft.a3amp.amp.Constants.Companion.SEND_CMD
import xyz.lazysoft.a3amp.amp.Constants.Companion.THR_DATA_SIZE
import xyz.lazysoft.a3amp.components.AmpComponent
import xyz.lazysoft.a3amp.midi.SysExMidiManager
import java.nio.ByteBuffer
import java.util.concurrent.Future
import java.util.logging.Logger
import kotlin.properties.Delegates
import android.os.AsyncTask.execute
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask


enum class AmpModel(val model: Byte) {
    THR5(0x30),
    THR10(0x31),
    THR10X(0x32),
    THR10C(0x33),
    THR5A(0x34)
}

class Amp(private val midiManager: SysExMidiManager) {
    private val logger = Logger.getLogger(this.javaClass.name)
    private var ampModel: AmpModel? by Delegates.observable<AmpModel?>(null)
    { _, old, new ->
        if (old != new) midiManager.sendSysExCmd(REQ_SETTINGS)
    }
    var modelAmpDetect: ((String) -> Unit)? = null
    var requestCallBack: ((ByteArray) -> Unit)? = null

    init {
        midiManager.sysExtListeners.add { data: ByteArray? ->
            if (data != null) {
                val sig = data.slice(IntRange(0, 6)).toByteArray()
                if (sig.contentEquals(HEART_BEAT)) {
                    val model = AmpModel.values().firstOrNull { it.model == data[7] }
                    if (model != null)
                        modelAmpDetect?.invoke(model.name)
                    ampModel = model
                } else if (data.size == THR_DATA_SIZE) { // config from amp
                    requestCallBack?.invoke(data)
                    requestCallBack = null
                    // chkeck header
                    // todo chk checksum
                    YdlDataConverter.dumpTo(data)
                            .forEach { midiManager.onMidiSystemExclusive(it) }
                }
            }
        }

    }

    fun getCurrrentDump(f: (ByteArray) -> Unit) {
        requestCallBack = f
        midiManager.sendSysExCmd(REQ_SETTINGS)
    }

    private fun paramToInt(param: ByteArray): Int {
        return ByteBuffer.wrap(param).short.toInt()
    }

    private fun intToParam(value: Int): ByteArray {
        return ByteBuffer.wrap(ByteArray(4))
                .putInt(value)
                .array().drop(2).toByteArray()
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
            midiManager.sendSysExCmd(sendCmd + intToParam(it) + END)
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
            midiManager.sendSysExCmd(cmdId + mode.toByte() + END)
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
            midiManager.sendSysExCmd(cmdId + it.toByte() + END)
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
            val value = if (it == 0) OFF else it - 1
            midiManager.sendSysExCmd(cmdId + value.toByte() + END)
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
            midiManager.sendSysExCmd(cmdId + value.toByte() + END)
        }
        return this
    }
}

