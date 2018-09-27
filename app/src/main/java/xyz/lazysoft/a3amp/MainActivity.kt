package xyz.lazysoft.a3amp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import xyz.lazysoft.a3amp.components.Amp
import xyz.lazysoft.a3amp.components.AmpKnobWrapper
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {
    //    F0 43 7D 10 41 30 01 XX XX XX F7
    val logger = Logger.getLogger("AmpComponent")

    private var midiManager = UsbMidiManager(this)
    private var thr = Amp(midiManager)

    private fun initSwithBlock(switch: Int, layout: Int) {
        val s = findViewById<Switch>(switch)
        val b = findViewById<FlexboxLayout>(layout)
        s.setOnCheckedChangeListener { _, isChecked ->
            b.visibility = if (isChecked) GONE else VISIBLE
        }
    }

    private fun initSpinner(spiner: Int, content: Int) {
        val adapter = ArrayAdapter.createFromResource(this,
                content, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        findViewById<Spinner>(spiner).adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        thr.addKnob(AmpKnobWrapper(findViewById(R.id.gain_knob)), Amp.K_GAIN)
                .addKnob(AmpKnobWrapper(findViewById(R.id.master_knob)), Amp.K_MASTER)
                .addKnob(AmpKnobWrapper(findViewById(R.id.bass_knob)), Amp.K_BASS)

        // amp model detect
        val ampNameText = findViewById<TextView>(R.id.amp_name)
        thr.modelAmpDetect = {s -> runOnUiThread{ ampNameText.text = s} }

        initSpinner(R.id.effect_spinner, R.array.effects)
        initSpinner(R.id.reverb_spinner, R.array.reverbs)

        initSwithBlock(R.id.compressor_switch, R.id.compressor_block)
        initSwithBlock(R.id.effect_switch, R.id.effect_block)
        initSwithBlock(R.id.delay_switch, R.id.delay_block)
        initSwithBlock(R.id.reverb_switch, R.id.reverb_block)
        initSwithBlock(R.id.gate_switch, R.id.gate_block)
        // midiManager.onMidiAttachedEvent = { b: Boolean -> knobs.enable = b }
        midiManager.open()

//        val switch = findViewById(R.id.effect_switch) as Switch
//        switch.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                // The toggle is enabled
//                sendCmd(ligthOn)
//            } else {
//                sendCmd(ligthOff)
//                // The toggle is disabled
//            }
//        }
//        button.setOnClickListener { view ->
//            Toast.makeText(view.context, "on click!", Toast.LENGTH_LONG).show()
//            val usbDeviceIterator = midiManager.usbDevices.iterator()
//            if (usbDeviceIterator.hasNext()) {
//                val devices = midiManager.getMidiOutputDevices(usbDeviceIterator.next())
//                devices.map { midiOutputDevice ->
//                    midiOutputDevice.sendMidiSystemExclusive(0, cmd)
//                }
//            } else {
//                Toast.makeText(view.context, "not found", Toast.LENGTH_LONG).show()
//            }
//        }
    }
}

