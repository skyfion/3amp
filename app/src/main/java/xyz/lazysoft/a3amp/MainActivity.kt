package xyz.lazysoft.a3amp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import jp.kshoji.driver.midi.util.UsbMidiDriver
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
//    F0 43 7D 10 41 30 01 XX XX XX F7

    private val cmm = arrayOf(0xF0, 0x43, 0x7D, 0x10, 0x41, 0x30, 0x01, 0x00, 0x00, 0x00, 0xF7).map { i -> i.toByte() }
    private val cmd: ByteArray = cmm.toByteArray()
    private var midiManager = UsbMidiManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        midiManager.open()

        button.setOnClickListener { view ->
            Toast.makeText(view.context, "on click!", Toast.LENGTH_LONG).show()
            val usbDeviceIterator = midiManager.usbDevices.iterator()
            if (usbDeviceIterator.hasNext()) {
                val devices = midiManager.getMidiOutputDevices(usbDeviceIterator.next())
                devices.map { midiOutputDevice ->
                    midiOutputDevice.sendMidiSystemExclusive(0, cmd)
                }
            } else {
                Toast.makeText(view.context, "not found", Toast.LENGTH_LONG).show()
            }
        }
    }
}

