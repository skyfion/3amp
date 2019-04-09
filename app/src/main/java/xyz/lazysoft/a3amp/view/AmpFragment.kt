package xyz.lazysoft.a3amp.view

import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.amp.Constants

class AmpFragment : AbstractThrFragment() {

    override fun initFragment() {
        initCarousel(R.id.amp_carousel, R.array.thr10_amps, Constants.AMP)
        initCarousel(R.id.cab_carousel, R.array.thr10_cabs, Constants.CAB)

        initKnob(R.id.gain_knob, R.id.gain_text)
        initKnob(R.id.master_knob, R.id.master_text)
        initKnob(R.id.bass_knob, R.id.bass_text)
        initKnob(R.id.treble_knob, R.id.treble_text)
        initKnob(R.id.middle_knob, R.id.middle_text)
//        thr     .addKnob(initKnob(R.id.gain_knob, R.id.gain_text), Constants.K_GAIN)
//                .addKnob(initKnob(R.id.master_knob, R.id.master_text), Constants.K_MASTER)
//                .addKnob(initKnob(R.id.bass_knob, R.id.bass_text), Constants.K_BASS)
//                .addKnob(initKnob(R.id.treble_knob, R.id.treble_text), Constants.K_TREB)
//                .addKnob(initKnob(R.id.middle_knob, R.id.middle_text), Constants.K_MID)
//                .addSpinner(initCarousel(R.id.amp_carousel, R.array.thr10_amps), Constants.AMP)
//                .addSpinner(initCarousel(R.id.cab_carousel, R.array.thr10_cabs), Constants.CAB)
    }

    override val fragmentId: Int
        get() = R.layout.amp

}