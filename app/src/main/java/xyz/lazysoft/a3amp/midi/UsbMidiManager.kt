package xyz.lazysoft.a3amp.midi

import android.content.Context
import android.hardware.usb.UsbDevice
import android.widget.Toast
import jp.kshoji.driver.midi.device.MidiInputDevice
import jp.kshoji.driver.midi.device.MidiOutputDevice
import jp.kshoji.driver.midi.util.UsbMidiDriver
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class UsbMidiManager(private val context: Context) : UsbMidiDriver(context) {

    private val usbDevices = HashSet<UsbDevice>()
    private val logger = Logger.getLogger(this.javaClass.name)

    override fun onMidiActiveSensing(p0: MidiInputDevice, p1: Int) {
    }

    override fun onMidiNoteOff(p0: MidiInputDevice, p1: Int, p2: Int, p3: Int, p4: Int) {
    }

    override fun onMidiChannelAftertouch(p0: MidiInputDevice, p1: Int, p2: Int, p3: Int) {
    }

    override fun onMidiInputDeviceDetached(p0: MidiInputDevice) {
        synchronized(usbDevices) {
            usbDevices.remove(p0.usbDevice)
        }
        midiAttached = false
    }

    override fun onMidiProgramChange(p0: MidiInputDevice, p1: Int, p2: Int, p3: Int) {
    }

    override fun onMidiSongPositionPointer(p0: MidiInputDevice, p1: Int, p2: Int) {
    }

    override fun onMidiPitchWheel(p0: MidiInputDevice, p1: Int, p2: Int, p3: Int) {
    }

    override fun onMidiTimeCodeQuarterFrame(p0: MidiInputDevice, p1: Int, p2: Int) {
    }

    override fun onMidiMiscellaneousFunctionCodes(p0: MidiInputDevice, p1: Int, p2: Int, p3: Int, p4: Int) {
    }

    override fun onMidiStart(p0: MidiInputDevice, p1: Int) {
    }

    override fun onMidiTimingClock(p0: MidiInputDevice, p1: Int) {
    }

    override fun onMidiOutputDeviceAttached(p0: MidiOutputDevice) {
    }

    var sysExtListeners: ArrayList<(ByteArray?) -> Unit> = ArrayList()

    fun onMidiSystemExclusive(cmd: ByteArray?) {
        logger.info("recive midi -> " + cmd?.joinToString())
        sysExtListeners.forEach { t: ((ByteArray?) -> Unit)? -> t?.invoke(cmd) }
    }

    override fun onMidiSystemExclusive(p0: MidiInputDevice, p1: Int, p2: ByteArray?) {
        onMidiSystemExclusive(p2)
    }

    override fun onMidiOutputDeviceDetached(p0: MidiOutputDevice) {
    }

    override fun onMidiSongSelect(p0: MidiInputDevice, p1: Int, p2: Int) {
    }

    override fun onMidiReset(p0: MidiInputDevice, p1: Int) {
    }

    override fun onMidiSystemCommonMessage(p0: MidiInputDevice, p1: Int, p2: ByteArray?) {
    }

    override fun onMidiSingleByte(p0: MidiInputDevice, p1: Int, p2: Int) {
    }

    override fun onMidiPolyphonicAftertouch(p0: MidiInputDevice, p1: Int, p2: Int, p3: Int, p4: Int) {
    }


    override fun onMidiCableEvents(p0: MidiInputDevice, p1: Int, p2: Int, p3: Int, p4: Int) {
    }

    override fun onMidiContinue(p0: MidiInputDevice, p1: Int) {
    }

    override fun onMidiNoteOn(p0: MidiInputDevice, p1: Int, p2: Int, p3: Int, p4: Int) {
    }

    override fun onMidiTuneRequest(p0: MidiInputDevice, p1: Int) {
    }

    override fun onMidiControlChange(p0: MidiInputDevice, p1: Int, p2: Int, p3: Int, p4: Int) {
    }

    override fun onMidiStop(p0: MidiInputDevice, p1: Int) {
    }

    override fun onMidiInputDeviceAttached(p0: MidiInputDevice) {
        synchronized(usbDevices) {
            usbDevices.add(p0.usbDevice)
        }
        midiAttached = true
        Toast.makeText(context, "midi attached!", Toast.LENGTH_SHORT).show()
    }

    override fun onDeviceDetached(p0: UsbDevice) {

    }

    var onMidiAttachedEvent: ((enable: Boolean) -> Unit)? = null

    var midiAttached: Boolean by Delegates.observable(false) { _, _, newValue ->
        onMidiAttachedEvent?.invoke(newValue)

    }

    override fun onDeviceAttached(p0: UsbDevice) {

    }

    fun sendSysExCmd(cmd: ByteArray) {
        val usbDeviceIterator = usbDevices.iterator()
        if (usbDeviceIterator.hasNext()) {
            val devices = getMidiOutputDevices(usbDeviceIterator.next())
            devices.map { midiOutputDevice ->
                midiOutputDevice.sendMidiSystemExclusive(0, cmd)
            }
        }
    }
}