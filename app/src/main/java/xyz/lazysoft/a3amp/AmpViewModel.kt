package xyz.lazysoft.a3amp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.amp.AmpCommand
import xyz.lazysoft.a3amp.persistence.AppDatabase
import javax.inject.Inject

class AmpViewModel : ViewModel() {
    @Inject
    lateinit var thr: Amp

    @Inject
    lateinit var repository: AppDatabase

    val receiveCommands: MutableLiveData<AmpCommand> = MutableLiveData()
    val sendCommands: MutableLiveData<AmpCommand> = MutableLiveData()

    init {
        thr.midiManager.sysExtListeners.add {
            AmpCommand.newInstance(it).let { cmd ->
                receiveCommands.value = cmd
            }
        }
    }

    fun sendCommand(cmd: ByteArray) {
        thr.midiManager.sendSysExCmd(cmd)
    }
}
