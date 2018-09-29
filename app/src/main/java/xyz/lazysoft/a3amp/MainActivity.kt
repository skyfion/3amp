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
import java.util.logging.Logger
import `in`.goodiebag.carouselpicker.CarouselPicker
import xyz.lazysoft.a3amp.components.*


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

    private fun initSpinner(spiner: Int, content: Int): AmpSpinner {
        val adapter = ArrayAdapter.createFromResource(this,
                content, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val s = findViewById<Spinner>(spiner)
        s.adapter = adapter
        return AmpSpinnerWrapper(s)
    }

    private fun initCarousel(carousel: Int, content: Int): AmpSpinner {
        val carouselPicker = findViewById<CarouselPicker>(carousel)
        val textItems = resources.getStringArray(content)
                .map { CarouselPicker.TextItem(it, 10) }

        val textAdapter = CarouselPicker.CarouselViewAdapter(this, textItems, 0)
        carouselPicker.adapter = textAdapter
        return AmpCarouselWrapper(carouselPicker)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        thr.addKnob(AmpKnobWrapper(findViewById(R.id.gain_knob)), Amp.K_GAIN)
                .addKnob(AmpKnobWrapper(findViewById(R.id.master_knob)), Amp.K_MASTER)
                .addKnob(AmpKnobWrapper(findViewById(R.id.bass_knob)), Amp.K_BASS)
                .addKnob(AmpKnobWrapper(findViewById(R.id.treble_knob)), Amp.K_TREB)
                .addKnob(AmpKnobWrapper(findViewById(R.id.middle_knob)), Amp.K_MID)

        // amp model detect
        val ampNameText = findViewById<TextView>(R.id.amp_name)
        thr.modelAmpDetect = {s -> runOnUiThread{ ampNameText.text = s} }

//        thr.addSpinner(initSpinner(R.id.amp_spinner, R.array.thr10_amps), Amp.AMP)
//        thr.addSpinner(initSpinner(R.id.cab_spinner, R.array.thr10_cabs), Amp.CAB)

       thr.addSpinner(initCarousel(R.id.amp_carousel, R.array.thr10_amps), Amp.AMP)
       thr.addSpinner(initCarousel(R.id.cab_carousel, R.array.thr10_cabs), Amp.CAB)

       // initSpinner(R.id.effect_spinner, R.array.effects)
       // initSpinner(R.id.reverb_spinner, R.array.reverbs)

//        initSwithBlock(R.id.compressor_switch, R.id.compressor_block)
//        initSwithBlock(R.id.effect_switch, R.id.effect_block)
//        initSwithBlock(R.id.delay_switch, R.id.delay_block)
//        initSwithBlock(R.id.reverb_switch, R.id.reverb_block)
//        initSwithBlock(R.id.gate_switch, R.id.gate_block)
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

