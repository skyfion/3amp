package xyz.lazysoft.a3amp.amp

import androidx.lifecycle.MutableLiveData
import xyz.lazysoft.a3amp.amp.Constants.Companion.HEART_BEAT
import xyz.lazysoft.a3amp.amp.Constants.Companion.REQ_SETTINGS
import xyz.lazysoft.a3amp.amp.Constants.Companion.TAG
import xyz.lazysoft.a3amp.amp.Constants.Companion.THR_DATA_SIZE
import xyz.lazysoft.a3amp.midi.SysExMidiManager
import xyz.lazysoft.a3amp.persistence.AmpPreset
import java.util.logging.Logger
import kotlin.properties.Delegates


enum class AmpModel(val id: Int) {
    THR5(0x30),
    THR10(0x31),
    THR10X(0x32),
    THR10C(0x33),
    THR5A(0x34)
}

class Amp(val midiManager: SysExMidiManager) {

    private val logger = Logger.getLogger(TAG)

    private var requestCallBack: ((ByteArray) -> Unit)? = null

    var modelAmpDetect: ((AmpModel) -> Unit)? = null

    private var ampModel: AmpModel? by Delegates.observable<AmpModel?>(null)
    { _, old, new ->
        if (old != new) midiManager.sendSysExCmd(REQ_SETTINGS)
    }

    val dump: MutableLiveData<PresetDump> by lazy {
        val result = MutableLiveData<PresetDump>()
        result.postValue(PresetDump(Constants.initPresetDump.toByteArray()))
        result
    }

    private var heartBeat: ByteArray? by Delegates.observable<ByteArray?>(null)
    { _, oldValue, newValue ->
        if ((oldValue == null && newValue != null) ||
                (newValue != null && oldValue != null && !newValue.contentEquals(oldValue))) {
            val model = AmpModel.values().firstOrNull { it.id == newValue[7].toInt() }
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
                } else if (data.size == THR_DATA_SIZE) { // config from thr
                    requestCallBack?.invoke(data)
                    requestCallBack = null
                    // chkeck header
                    // todo chk checksum
                    applyDump(data)
                }
            }
        }

        midiManager.sendSysExtListeners.add {
            writeCommand(it)
        }

    }

    private fun writeCommand(it: ByteArray?) {
        if (it != null && it.size > 9) {
            val cmd = it.slice(IntRange(0, 6)).toByteArray()
            if (cmd.contentEquals(Constants.SEND_CMD)) {
                dump.value?.let { d ->
                    d.writeDump(it[7].toInt(), Pair(it[8], it[9]))
                    dump.postValue(d)
                }
            }
        }
    }

    fun open() {
        // todo remove this ?
        // init empty dump
//        YdlDataConverter.thr5and10(dump.value!!.toList()).forEach { cmd ->
//            midiManager.onMidiSystemExclusive(cmd)
//        }

    }

    private fun applyDump(dump: ByteArray) {
        YdlDataConverter.dumpTo(dump)
                .forEach { midiManager.onMidiSystemExclusive(it) }
    }

    fun sendCommand(cmd: ByteArray) {
        midiManager.sendSysExCmd(cmd)
    }


//    fun addSwSpinner(carousel: AmpComponent<Int>, swId: Int): Amp {
//        val cmdId = SEND_CMD + swId.toByte() + 0x00
//        midiManager.sysExtListeners.add {
//            if (it != null && it.size > 9) {
//                val cmd = it.slice(IntRange(0, 8)).toByteArray()
//                if (cmd.contentEquals(cmdId)) {
//                    carousel.state = if (it[9] == OFF.toByte()) 0 else 1
//                }
//            }
//        }
//        carousel.setOnStateChanged {
//            val value = if (it == 0) OFF else ON
//            midiManager.sendSysExCmd(cmdId + value.toByte() + END.toByte())
//        }
//        return this
//    }

    var selectPreset: AmpPreset? = null
        set(value) {
            value?.let { loadPreset(it) }
            field = value
        }

    val dumpState: PresetDump
        get() = dump.value!!

    private fun loadPreset(preset: AmpPreset) {
        preset.dump.let {
            //  val cmd = Constants.HEAD + Constants.DUMP_PREFIX + 0x31 + it + Constants.DUMP_POSTFIX + 0x1F + Constants.END
            //todo need unit test
            // logger.info("load dump -> " + it.joinToString())

            // todo maybe send one command
            YdlDataConverter.thr5and10(it.toList()).forEach { cmd ->
                midiManager.onMidiSystemExclusive(cmd)
            }

            dump.postValue(PresetDump(it))
        }
    }
}

