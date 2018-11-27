package xyz.lazysoft.a3amp.midi

interface SysExMidiManager {

    var sysExtListeners: ArrayList<(ByteArray?) -> Unit>

    fun sendSysExCmd(cmd: ByteArray)

    fun onMidiSystemExclusive(cmd: ByteArray?) {
        sysExtListeners.forEach { it(cmd) }
    }

}