package xyz.lazysoft.a3amp.view

import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.amp.Constants

class AmpFragment : AbstractThrFragment() {

    override fun initFragment() {
        initCarousel(R.id.amp_carousel, R.array.thr10_amps, Constants.AMP)
        initCarousel(R.id.cab_carousel, R.array.thr10_cabs, Constants.CAB)

        initKnob(find(R.id.gain_knob), find(R.id.gain_text), Constants.K_GAIN)
        initKnob(find(R.id.master_knob), find(R.id.master_text), Constants.K_MASTER)
        initKnob(find(R.id.bass_knob), find(R.id.bass_text), Constants.K_BASS)
        initKnob(find(R.id.treble_knob), find(R.id.treble_text), Constants.K_TREB)
        initKnob(find(R.id.middle_knob), find(R.id.middle_text), Constants.K_MID)
    }

    override val fragmentId: Int
        get() = R.layout.amp

}