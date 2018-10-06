package xyz.lazysoft.a3amp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import `in`.goodiebag.carouselpicker.CarouselPicker
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import xyz.lazysoft.a3amp.components.*


class MainActivity : AppCompatActivity() {

    private var midiManager = UsbMidiManager(this)
    private var thr = Amp(midiManager)

    private fun initSwitchBlock(switch: Int, changeListener: ((value: Boolean) -> Unit)?): AmpComponent<Boolean> {
        val s = findViewById<Switch>(switch)
        val ampSwitchWrapper = AmpSwitchWrapper(s)
        if (changeListener != null)
            ampSwitchWrapper.setOnStateChanged(changeListener)
        return ampSwitchWrapper
    }

    private fun initSpinner(spiner: Int, content: Int): AmpComponent<Int> {
        val adapter = ArrayAdapter.createFromResource(this,
                content, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val s = findViewById<Spinner>(spiner)
        s.adapter = adapter
        return AmpSpinnerWrapper(s)
    }

    private fun blockActivator(vararg ids: Int): (value: Int) -> Unit {
        return { value ->
            ids.map { findViewById<View>(it) }
                    .withIndex()
                    .forEach { (index, view) -> view.visibility = if (index == value) VISIBLE else GONE }
        }
    }

    private fun initCarousel(carousel: Int, content: Int): AmpComponent<Int> {
        return initCarousel(carousel, content, null)
    }

    private fun initCarousel(carousel: Int, content: Int, changeListener: ((mode: Int) -> Unit)?): AmpComponent<Int> {
        val carouselPicker = findViewById<CarouselPicker>(carousel)
        val textItems = resources.getStringArray(content)
                .map { CarouselPicker.TextItem(it, 10) }
        carouselPicker.adapter = CarouselPicker.CarouselViewAdapter(this, textItems, 0)
        val ampCarouselWrapper = AmpCarouselWrapper(carouselPicker)
        if (changeListener != null)
            ampCarouselWrapper.setOnStateChanged(changeListener)
        return ampCarouselWrapper
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initCarousel(R.id.tabs_carousel, R.array.tabs,
                blockActivator(
                        R.id.tab_amp,
                        R.id.tab_compressor,
                        R.id.tab_effects,
                        R.id.tab_delay,
                        R.id.tab_reverb,
                        R.id.tab_gate))

        thr.addKnob(AmpKnobWrapper(findViewById(R.id.gain_knob)), Amp.K_GAIN)
                .addKnob(AmpKnobWrapper(findViewById(R.id.master_knob)), Amp.K_MASTER)
                .addKnob(AmpKnobWrapper(findViewById(R.id.bass_knob)), Amp.K_BASS)
                .addKnob(AmpKnobWrapper(findViewById(R.id.treble_knob)), Amp.K_TREB)
                .addKnob(AmpKnobWrapper(findViewById(R.id.middle_knob)), Amp.K_MID)


        // amp model detect
        val ampNameText = findViewById<TextView>(R.id.amp_name)
        thr.modelAmpDetect = { s -> runOnUiThread { ampNameText.text = s } }

        thr.addSpinner(initCarousel(R.id.amp_carousel, R.array.thr10_amps), Amp.AMP)
        thr.addSpinner(initCarousel(R.id.cab_carousel, R.array.thr10_cabs), Amp.CAB)

        // compressor
        thr.addKnob(
                AmpKnobWrapper(findViewById(R.id.compressor_output_knob)),
                Amp.COMPRESSOR_STOMP_OUTPUT)
                .addKnob(
                        AmpKnobWrapper(findViewById(R.id.compressor_sustain_knob)),
                        Amp.COMPRESSOR_STOMP_SUSTAIN)
                .addOffSpinner(initCarousel(R.id.compressor_mode_carousel,
                        R.array.compressor_modes
                        , blockActivator(R.id.compressor_empty_block,
                        R.id.compressor_stomp, R.id.compressor_rack))
                        , Amp.COMPRESSOR_MODE, Amp.COMPRESSOR_SW)

        // effects
        thr.addOffSpinner(initCarousel(R.id.effect_mode_carousel, R.array.effects,
                blockActivator(
                        R.id.effect_empty_block,
                        R.id.effect_chorus_block,
                        R.id.effect_flanger_block,
                        R.id.effect_tremolo_block,
                        R.id.effect_phaser_block)), Amp.EFFECTS_MODE, Amp.EFFECTS_SW)
                // chorus
                .addKnob(AmpKnobWrapper(findViewById(R.id.chorus_speed_knob)), Amp.EFFECT_KNOB1)
                .addKnob(AmpKnobWrapper(findViewById(R.id.chorus_depth_knob)), Amp.EFFECT_KNOB2)
                .addKnob(AmpKnobWrapper(findViewById(R.id.chorus_mix_knob)), Amp.EFFECT_KNOB3)
                // flanger
                .addKnob(AmpKnobWrapper(findViewById(R.id.flanger_speed_knob)), Amp.EFFECT_KNOB1)
                .addKnob(AmpKnobWrapper(findViewById(R.id.flanger_manual_knob)), Amp.EFFECT_KNOB2)
                .addKnob(AmpKnobWrapper(findViewById(R.id.flanger_depth_knob)), Amp.EFFECT_KNOB3)
                .addKnob(AmpKnobWrapper(findViewById(R.id.flanger_feedback_knob)), Amp.EFFECT_KNOB4)
                .addKnob(AmpKnobWrapper(findViewById(R.id.flanger_spread_knob)), Amp.EFFECT_KNOB5)
                // tremolo
                .addKnob(AmpKnobWrapper(findViewById(R.id.tremolo_freq_knob)), Amp.EFFECT_KNOB1)
                .addKnob(AmpKnobWrapper(findViewById(R.id.tremolo_depth_knob)), Amp.EFFECT_KNOB2)
                // phaser
                .addKnob(AmpKnobWrapper(findViewById(R.id.phaser_speed_knob)), Amp.EFFECT_KNOB1)
                .addKnob(AmpKnobWrapper(findViewById(R.id.phaser_manual_knob)), Amp.EFFECT_KNOB2)
                .addKnob(AmpKnobWrapper(findViewById(R.id.phaser_depth_knob)), Amp.EFFECT_KNOB3)
                .addKnob(AmpKnobWrapper(findViewById(R.id.phaser_feedback_knob)), Amp.EFFECT_KNOB4)

        // delay
        thr.addSwSpinner(initCarousel(R.id.delay_sw_carousel, R.array.sw_modes,
                blockActivator(R.id.delay_empty_block, R.id.delay_block)), Amp.DELAY_SW)

        // reverb
        thr.addOffSpinner(initCarousel(R.id.reverb_mode_carousel, R.array.reverbs),
                Amp.REVERB_MODE, Amp.REVERB_SW)
        // gate
        thr.addSwSpinner(initCarousel(R.id.gate_carousel, R.array.sw_modes,
                blockActivator(R.id.gate_empty_block, R.id.gate_block)), Amp.GATE_SW)
                .addKnob(AmpKnobWrapper(findViewById(R.id.gate_release_knob)), Amp.GATE_RELEASE)
                .addKnob(AmpKnobWrapper(findViewById(R.id.gate_threshold_knob)), Amp.GATE_THRESHOLD)



        midiManager.open()

    }
}

