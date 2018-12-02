package xyz.lazysoft.a3amp.midi

interface SysExMidiManager {

    var sysExtListeners: ArrayList<(ByteArray?) -> Unit>
    var sendSysExtListeners: ArrayList<(ByteArray?) -> Unit>

    fun sendSysExCmd(cmd: ByteArray) {
        sendSysExtListeners.forEach { it(cmd) }
    }

    fun onMidiSystemExclusive(cmd: ByteArray?) {
        sysExtListeners.forEach { it(cmd) }
    }

}