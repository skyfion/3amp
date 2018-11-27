package xyz.lazysoft.a3amp.midi

import android.annotation.TargetApi
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast


@TargetApi(Build.VERSION_CODES.M)
class AndroidMidiManager(context: Context): SysExMidiManager {

   // var deviceInfo: MidiDeviceInfo? = null
    var device: MidiDevice? = null
    var inputPort: MidiInputPort? = null

    init {
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            Log.i(TAG, "work!")
            val midiManager = context.getSystemService(Context.MIDI_SERVICE) as MidiManager
            midiManager.registerDeviceCallback(object : MidiManager.DeviceCallback() {
                override fun onDeviceRemoved(device: MidiDeviceInfo?) {
                    //  deviceInfo = null
                    super.onDeviceRemoved(device)
                }

                override fun onDeviceAdded(deviceInfo: MidiDeviceInfo?) {
                    deviceInfo?.let {
                        midiManager.openDevice(deviceInfo,
                                { midiDevice ->
                                    if (midiDevice != null) {
                                        val portInfos = it.ports
                                        if (portInfos[0].type == MidiDeviceInfo.PortInfo.TYPE_INPUT) {
                                            inputPort = midiDevice.openInputPort(portInfos[0].portNumber)
                                            Toast.makeText(context, "open device $deviceInfo",
                                                    Toast.LENGTH_SHORT).show()
                                        }
                                        device = midiDevice
                                    } else {
                                        Toast.makeText(context, "could not open device $deviceInfo",
                                                Toast.LENGTH_SHORT).show()
                                    }
                                },
                                Handler(Looper.getMainLooper()))
                    }

                    super.onDeviceAdded(deviceInfo)
                }
            }, Handler(Looper.getMainLooper()))
        }

    }

    override var sysExtListeners: ArrayList<(ByteArray?) -> Unit> = ArrayList()

    override fun sendSysExCmd(cmd: ByteArray) {
        inputPort?.send(cmd, 0, 0)
    }


}