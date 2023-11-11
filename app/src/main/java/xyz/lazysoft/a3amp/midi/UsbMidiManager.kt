package xyz.lazysoft.a3amp.midi

import android.content.Context
import android.hardware.usb.UsbDevice
import jp.kshoji.driver.midi.device.MidiInputDevice
import jp.kshoji.driver.midi.device.MidiOutputDevice
import jp.kshoji.driver.midi.util.UsbMidiDriver

abstract class UsbMidiManager(context: Context) : UsbMidiDriver(context), SysExMidiManager {

    override fun onMidiActiveSensing(p0: MidiInputDevice, p1: Int) {
    }

    override fun onMidiNoteOff(p0: MidiInputDevice, p1: Int, p2: Int, p3: Int, p4: Int) {
    }

    override fun onMidiChannelAftertouch(p0: MidiInputDevice, p1: Int, p2: Int, p3: Int) {
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



    @Deprecated("Deprecated in Java")
    override fun onDeviceDetached(p0: UsbDevice) {

    }



    @Deprecated("Deprecated in Java")
    override fun onDeviceAttached(p0: UsbDevice) {

    }


}