package xyz.lazysoft.a3amp.midi

import android.content.Context
import android.hardware.usb.UsbDevice
import android.widget.Toast
import jp.kshoji.driver.midi.device.MidiInputDevice
import xyz.lazysoft.a3amp.amp.Constants.Companion.TAG
import java.util.HashSet
import java.util.logging.Logger
import kotlin.properties.Delegates

/**
 * https://github.com/kshoji/USB-MIDI-Driver
 */
class AmpMidiManager(private val context: Context) : UsbMidiManager(context) {
    private val logger = Logger.getLogger(TAG)
    private val usbDevices = HashSet<UsbDevice>()
    private var onMidiAttachedEvent: ((enable: Boolean) -> Unit)? = null

    override var sendSysExtListeners: ArrayList<(ByteArray?) -> Unit> = ArrayList()
    override var sysExtListeners: ArrayList<(ByteArray?) -> Unit> = ArrayList()

    private var midiAttached: Boolean by Delegates.observable(false) { _, _, newValue ->
        onMidiAttachedEvent?.invoke(newValue)
    }

    override fun onMidiInputDeviceDetached(p0: MidiInputDevice) {
        synchronized(usbDevices) {
            usbDevices.remove(p0.usbDevice)
        }
        midiAttached = false
    }

    override fun onMidiSystemExclusive(p0: MidiInputDevice, p1: Int, p2: ByteArray?) {
        onMidiSystemExclusive(p2)
    }

    override fun onMidiInputDeviceAttached(p0: MidiInputDevice) {
        synchronized(usbDevices) {
            usbDevices.add(p0.usbDevice)
        }
        midiAttached = true
        Toast.makeText(context, "midi attached!", Toast.LENGTH_SHORT).show()
    }

    override fun sendSysExCmd(cmd: ByteArray) {
        super.sendSysExCmd(cmd)
        val usbDeviceIterator = usbDevices.iterator()
        if (usbDeviceIterator.hasNext()) {
            val devices = getMidiOutputDevices(usbDeviceIterator.next())
            devices.map { midiOutputDevice ->
                midiOutputDevice.sendMidiSystemExclusive(0, cmd)
            }
        }
    }
}