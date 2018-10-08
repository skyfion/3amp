package xyz.lazysoft.a3amp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import `in`.goodiebag.carouselpicker.CarouselPicker
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import kotlinx.android.synthetic.main.amp.view.*
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

    private fun initKnob(knob: Int, text: Int): AmpComponent<Int> {
        return initKnob(knob, text, null)
    }

    private fun initKnob(knob: Int, text: Int, factor: Int?): AmpComponent<Int> {
        val ampKnobWrapper = AmpKnobWrapper(findViewById(knob))
        val knobText = findViewById<TextView>(text)
        ampKnobWrapper.factor = factor
        ampKnobWrapper.setOnStateChanged {
            knobText.text = it.toString()
        }
        return ampKnobWrapper
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

        thr.addKnob(initKnob(R.id.gain_knob, R.id.gain_text), Amp.K_GAIN)
                .addKnob(initKnob(R.id.master_knob, R.id.master_text), Amp.K_MASTER)
                .addKnob(initKnob(R.id.bass_knob, R.id.bass_text), Amp.K_BASS)
                .addKnob(initKnob(R.id.treble_knob, R.id.treble_text), Amp.K_TREB)
                .addKnob(initKnob(R.id.middle_knob, R.id.middle_text), Amp.K_MID)

        // amp model detect
        val ampNameText = findViewById<TextView>(R.id.amp_name)
        thr.modelAmpDetect = { s -> runOnUiThread { ampNameText.text = s } }

        thr.addSpinner(initCarousel(R.id.amp_carousel, R.array.thr10_amps), Amp.AMP)
        thr.addSpinner(initCarousel(R.id.cab_carousel, R.array.thr10_cabs), Amp.CAB)

        // compressor
        thr.addKnob(
                initKnob(R.id.compressor_output_knob, R.id.c_output_text),
                Amp.COMPRESSOR_STOMP_OUTPUT)
                .addKnob(initKnob(R.id.compressor_sustain_knob, R.id.c_sustain_text),
                        Amp.COMPRESSOR_STOMP_SUSTAIN)
                .addOffSpinner(initCarousel(R.id.compressor_mode_carousel,
                        R.array.compressor_modes
                        , blockActivator(R.id.compressor_empty_block,
                        R.id.compressor_stomp, R.id.compressor_rack))
                        , Amp.COMPRESSOR_MODE, Amp.COMPRESSOR_SW)
                // rack
                .addKnob(initKnob(R.id.c_threshold_knob, R.id.c_rack_threshold_text, 6),
                        Amp.COMPRESSOR_RACK_THRESHOLD)
                .addKnob(initKnob(R.id.c_attack_knob, R.id.c_rack_attack_text),
                        Amp.COMPRESSOR_RACK_ATTACK)
                .addKnob(initKnob(R.id.c_release_knob, R.id.c_release_text),
                        Amp.COMPRESSOR_RACK_RELEASE)
                .addKnob(initKnob(R.id.c_rack_output_knob, R.id.c_rack_output_text, 6),
                        Amp.COMPRESSOR_RACK_OUTPUT)
                .addSpinner(initCarousel(R.id.compressor_knee_carousel, R.array.knee),
                        Amp.COMPRESSOR_RACK_KNEE)
                .addSpinner(initCarousel(R.id.compressor_ratio_carousel, R.array.ratio),
                        Amp.COMPRESSOR_RACK_RATIO)


        // effects
        thr.addOffSpinner(initCarousel(R.id.effect_mode_carousel, R.array.effects,
                blockActivator(
                        R.id.effect_empty_block,
                        R.id.effect_chorus_block,
                        R.id.effect_flanger_block,
                        R.id.effect_tremolo_block,
                        R.id.effect_phaser_block)), Amp.EFFECTS_MODE, Amp.EFFECTS_SW)
                // chorus
                .addKnob(initKnob(R.id.chorus_speed_knob, R.id.chorus_speed_text), Amp.EFFECT_KNOB1)
                .addKnob(initKnob(R.id.chorus_depth_knob, R.id.chorus_depth_text), Amp.EFFECT_KNOB2)
                .addKnob(initKnob(R.id.chorus_mix_knob, R.id.chorus_mix_text), Amp.EFFECT_KNOB3)
                // flanger
                .addKnob(initKnob(R.id.flanger_speed_knob, R.id.flanger_speed_text), Amp.EFFECT_KNOB1)
                .addKnob(initKnob(R.id.flanger_manual_knob, R.id.flanger_manual_text), Amp.EFFECT_KNOB2)
                .addKnob(initKnob(R.id.flanger_depth_knob, R.id.flanger_depth_text), Amp.EFFECT_KNOB3)
                .addKnob(initKnob(R.id.flanger_feedback_knob, R.id.flanger_feedback_text), Amp.EFFECT_KNOB4)
                .addKnob(initKnob(R.id.flanger_spread_knob, R.id.flanger_spread_text), Amp.EFFECT_KNOB5)
                // tremolo
                .addKnob(initKnob(R.id.tremolo_freq_knob, R.id.tremolo_freq_text), Amp.EFFECT_KNOB1)
                .addKnob(initKnob(R.id.tremolo_depth_knob, R.id.tremolo_depth_text), Amp.EFFECT_KNOB2)
                // phaser
                .addKnob(initKnob(R.id.phaser_speed_knob, R.id.phaser_speed_text), Amp.EFFECT_KNOB1)
                .addKnob(initKnob(R.id.phaser_manual_knob, R.id.phaser_manual_text), Amp.EFFECT_KNOB2)
                .addKnob(initKnob(R.id.phaser_depth_knob, R.id.phaser_depth_text), Amp.EFFECT_KNOB3)
                .addKnob(initKnob(R.id.phaser_feedback_knob, R.id.phaser_feedback_text), Amp.EFFECT_KNOB4)

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

