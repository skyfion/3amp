package xyz.lazysoft.a3amp

import `in`.goodiebag.carouselpicker.CarouselPicker
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import it.beppi.knoblibrary.Knob
import xyz.lazysoft.a3amp.amp.Constants
import xyz.lazysoft.a3amp.components.wrappers.AmpComponent
import xyz.lazysoft.a3amp.components.wrappers.AmpCarouselWrapper
import xyz.lazysoft.a3amp.components.wrappers.AmpKnobWrapper


class AmpFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var viewModel: AmpViewModel
    private val knobs = mutableListOf<AmpKnobWrapper>()
    private lateinit var components: ThrComponents

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key != null && key == SettingsActivity.KNOB_MODE) {
            setKnobMode(sharedPreferences?.getString(key, Knob.SWIPEDIRECTION_CIRCULAR.toString()))
        }
    }

    fun setKnobMode(mode: String?) {
        mode?.let {
            knobs.map { k -> k.knob.swipeDirection = it.toInt() }
        }
    }

    private fun <T : View> findViewById(id: Int): T {
        return view!!.findViewById(id)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.amp_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AmpViewModel::class.java)
        components = ThrComponents { viewModel.sendCommand(it)}
        viewModel.receiveCommands.observe(this, Observer {
            components.receiveCommand(it)
        })
        initAmp()
    }

    private fun initKnob(knob: Int, text: Int): AmpComponent<Int> {
        return initKnob(knob, text, null)
    }

    private fun initKnob(knob: Int, text: Int, range: Pair<Int, Int>?): AmpComponent<Int> {
        val ampKnobWrapper = AmpKnobWrapper(findViewById(knob), range)
        val knobText = findViewById<TextView>(text)

        ampKnobWrapper.setOnStateChanged {
            knobText.text = it.toString()
        }
        knobs.add(ampKnobWrapper)
        return ampKnobWrapper
    }

    private fun blockActivator(vararg ids: Int): (value: Int) -> Unit {
        return { value ->
            ids.map { findViewById<View>(it) }
                    .withIndex()
                    .forEach { (index, view) -> view.visibility = if (index == value) View.VISIBLE else View.GONE }
        }
    }

    private fun initCarousel(carousel: Int, content: Int): AmpComponent<Int> {
        return initCarousel(carousel, content, null)
    }

    private fun initCarousel(carousel: Int, content: Int,
                             changeListener: ((mode: Int) -> Unit)?): AmpComponent<Int> {
        val carouselPicker = findViewById<CarouselPicker>(carousel)
        val ampCarouselWrapper = AmpCarouselWrapper(carouselPicker)
        ampCarouselWrapper.setContent(content, activity!!)
        if (changeListener != null)
            ampCarouselWrapper.setOnStateChanged(changeListener)
        return ampCarouselWrapper
    }

    private fun initAmp() {
        initCarousel(R.id.tabs_carousel, R.array.tabs,
                blockActivator(R.id.tab_amp, R.id.tab_compressor,
                        R.id.tab_effects, R.id.tab_delay,
                        R.id.tab_reverb, R.id.tab_gate))

        val ampMode = initCarousel(R.id.amp_carousel, R.array.thr10_amps)
        val cabMode = initCarousel(R.id.cab_carousel, R.array.thr10_cabs)

        components.addKnob(initKnob(R.id.gain_knob, R.id.gain_text), Constants.K_GAIN)
                .addKnob(initKnob(R.id.master_knob, R.id.master_text), Constants.K_MASTER)
                .addKnob(initKnob(R.id.bass_knob, R.id.bass_text), Constants.K_BASS)
                .addKnob(initKnob(R.id.treble_knob, R.id.treble_text), Constants.K_TREB)
                .addKnob(initKnob(R.id.middle_knob, R.id.middle_text), Constants.K_MID)
                .addSpinner(ampMode, Constants.AMP)
                .addSpinner(cabMode, Constants.CAB)

        // amp id detect
        // todo implement this
//        thr.modelAmpDetect = { model ->
//            runOnUiThread {
//
//                val thrMode: Int? = when (model) {
//                    AmpModel.THR10X -> R.array.thr10x_amps
//                    AmpModel.THR10 -> R.array.thr10_amps
//                    AmpModel.THR10C -> R.array.thr10c_amps
//                    AmpModel.THR5 -> R.array.thr5_amps
//                    AmpModel.THR5A -> R.array.thr5_amps
//                }
//
//                thrMode?.let {
//                    (ampMode as AmpCarouselWrapper).setContent(it, this)
//                }
//
//                val toolbar = findViewById<Toolbar>(R.id.amp_toolbar)
//                if (toolbar != null) {
//                    toolbar.title = "${resources.getString(R.string.app_name)} - ${model.name}"
//                }
//            }
//        }

        // compressor
        components.addKnob(
                initKnob(R.id.compressor_output_knob, R.id.c_output_text),
                Constants.COMPRESSOR_STOMP_OUTPUT)
                .addKnob(initKnob(R.id.compressor_sustain_knob, R.id.c_sustain_text),
                        Constants.COMPRESSOR_STOMP_SUSTAIN)
                .addOffSpinner(initCarousel(R.id.compressor_mode_carousel,
                        R.array.compressor_modes
                        , blockActivator(R.id.compressor_empty_block,
                        R.id.compressor_stomp, R.id.compressor_rack))
                        , Constants.COMPRESSOR_MODE, Constants.COMPRESSOR_SW)
                // rack
                .addKnob(initKnob(R.id.c_threshold_knob, R.id.c_rack_threshold_text, Pair(0, 600)),
                        Constants.COMPRESSOR_RACK_THRESHOLD)
                .addKnob(initKnob(R.id.c_attack_knob, R.id.c_rack_attack_text),
                        Constants.COMPRESSOR_RACK_ATTACK)
                .addKnob(initKnob(R.id.c_release_knob, R.id.c_release_text),
                        Constants.COMPRESSOR_RACK_RELEASE)
                .addKnob(initKnob(R.id.c_rack_output_knob, R.id.c_rack_output_text, Pair(0, 600)),
                        Constants.COMPRESSOR_RACK_OUTPUT)
                .addSpinner(initCarousel(R.id.compressor_knee_carousel, R.array.knee),
                        Constants.COMPRESSOR_RACK_KNEE)
                .addSpinner(initCarousel(R.id.compressor_ratio_carousel, R.array.ratio),
                        Constants.COMPRESSOR_RACK_RATIO)


        // effects
        components.addOffSpinner(initCarousel(R.id.effect_mode_carousel, R.array.effects,
                blockActivator(
                        R.id.effect_empty_block,
                        R.id.effect_chorus_block,
                        R.id.effect_flanger_block,
                        R.id.effect_tremolo_block,
                        R.id.effect_phaser_block)), Constants.EFFECTS_MODE, Constants.EFFECTS_SW)
                // chorus
                .addKnob(initKnob(R.id.chorus_speed_knob,
                        R.id.chorus_speed_text), Constants.EFFECT_KNOB1)
                .addKnob(initKnob(R.id.chorus_depth_knob,
                        R.id.chorus_depth_text), Constants.EFFECT_KNOB2)
                .addKnob(initKnob(R.id.chorus_mix_knob,
                        R.id.chorus_mix_text), Constants.EFFECT_KNOB3)
                // flanger
                .addKnob(initKnob(R.id.flanger_speed_knob,
                        R.id.flanger_speed_text), Constants.EFFECT_KNOB1)
                .addKnob(initKnob(R.id.flanger_manual_knob,
                        R.id.flanger_manual_text), Constants.EFFECT_KNOB2)
                .addKnob(initKnob(R.id.flanger_depth_knob,
                        R.id.flanger_depth_text), Constants.EFFECT_KNOB3)
                .addKnob(initKnob(R.id.flanger_feedback_knob,
                        R.id.flanger_feedback_text), Constants.EFFECT_KNOB4)
                .addKnob(initKnob(R.id.flanger_spread_knob,
                        R.id.flanger_spread_text), Constants.EFFECT_KNOB5)
                // tremolo
                .addKnob(initKnob(R.id.tremolo_freq_knob,
                        R.id.tremolo_freq_text), Constants.EFFECT_KNOB1)
                .addKnob(initKnob(R.id.tremolo_depth_knob,
                        R.id.tremolo_depth_text), Constants.EFFECT_KNOB2)
                // phaser
                .addKnob(initKnob(R.id.phaser_speed_knob,
                        R.id.phaser_speed_text), Constants.EFFECT_KNOB1)
                .addKnob(initKnob(R.id.phaser_manual_knob,
                        R.id.phaser_manual_text), Constants.EFFECT_KNOB2)
                .addKnob(initKnob(R.id.phaser_depth_knob,
                        R.id.phaser_depth_text), Constants.EFFECT_KNOB3)
                .addKnob(initKnob(R.id.phaser_feedback_knob,
                        R.id.phaser_feedback_text), Constants.EFFECT_KNOB4)

        // delay
        components.addSwSpinner(initCarousel(R.id.delay_sw_carousel, R.array.sw_modes,
                blockActivator(R.id.delay_empty_block,
                        R.id.delay_block)), Constants.DELAY_SW)
                .addKnob(initKnob(R.id.delay_feedback_knob,
                        R.id.delay_feedback_text), Constants.DELAY_FEEDBACK)
                .addKnob(initKnob(R.id.delay_level_knob,
                        R.id.delay_level_text), Constants.DELAY_LEVEL)
                .addKnob(initKnob(R.id.delay_time_knob,
                        R.id.delay_time_text, Pair(1, 9999)), Constants.DELAY_TIME)
                .addKnob(initKnob(R.id.delay_high_cut_knob,
                        R.id.delay_high_cut_text, Pair(1000, 16001)), Constants.DELAY_HIGH_CUT)
                .addKnob(initKnob(R.id.delay_low_cut_knob,
                        R.id.delay_low_cut_text, Pair(21, 8000)), Constants.DELAY_LOW_CUT)

        // reverb
        components.addOffSpinner(initCarousel(R.id.reverb_mode_carousel, R.array.reverbs)
        { value ->
            sequenceOf(R.id.reverb_empty_block,
                    R.id.common_reverb_block, R.id.spring_reverb_block)
                    .map { findViewById<View>(it) }
                    .withIndex().forEach {
                        val index = when (value) {
                            in 1..3 -> 1
                            4 -> 2
                            else -> 0
                        }
                        it.value.visibility = if (index == it.index) View.VISIBLE else View.GONE
                    }
        },
                Constants.REVERB_MODE, Constants.REVERB_SW)
                .addKnob(initKnob(R.id.reverb_time_knob,
                        R.id.reverb_time_text, Pair(3, 200)), Constants.REVERB_TIME)
                .addKnob(initKnob(R.id.reverb_pre_delay_knob,
                        R.id.reverb_pre_dalay_text, Pair(1, 2000)), Constants.REVERB_PRE_DELAY)
                .addKnob(initKnob(R.id.reverb_low_cut,
                        R.id.reverb_low_cut_text, Pair(21, 8000)), Constants.REVERB_LOW_CUT)
                .addKnob(initKnob(R.id.reverb_hi_cut_knob,
                        R.id.reverb_high_cut_text, Pair(1000, 16001)), Constants.REVERB_HIGH_CUT)
                .addKnob(initKnob(R.id.reverb_hi_ratio_knob,
                        R.id.reverb_high_ratio_text, Pair(1, 10)), Constants.REVERB_HIGH_RATIO)
                .addKnob(initKnob(R.id.reverb_low_ratio_knob,
                        R.id.reverb_low_ratio_text, Pair(1, 14)), Constants.REVERB_LOW_RATIO)
                .addKnob(initKnob(R.id.reverb_level_knob,
                        R.id.reverb_level_text), Constants.REVERB_LEVEL)
                .addKnob(initKnob(R.id.reverb_spring_reverb,
                        R.id.reverb_spring_reverb_text), Constants.REVERB_TIME)
                .addKnob(initKnob(R.id.reverb_spring_filter_knob,
                        R.id.reverb_spring_filter_text), Constants.REVERB_SPRING_FILTER)
        // gate
        components.addSwSpinner(initCarousel(R.id.gate_carousel, R.array.sw_modes,
                blockActivator(R.id.gate_empty_block, R.id.gate_block)), Constants.GATE_SW)
                .addKnob(initKnob(R.id.gate_release_knob,
                        R.id.gate_release_text), Constants.GATE_RELEASE)
                .addKnob(initKnob(R.id.gate_threshold_knob,
                        R.id.gate_threshold_text), Constants.GATE_THRESHOLD)

    }

    companion object {
        fun newInstance() = AmpFragment()

    }
}
