package xyz.lazysoft.a3amp.view

import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.amp.Constants

class AmpFragment : AbstractThrFragment() {

    override fun initFragment() {
        val ampMode = initCarousel(R.id.amp_carousel, R.array.thr10_amps)
        val cabMode = initCarousel(R.id.cab_carousel, R.array.thr10_cabs)

        thr.addKnob(initKnob(R.id.gain_knob, R.id.gain_text), Constants.K_GAIN)
                .addKnob(initKnob(R.id.master_knob, R.id.master_text), Constants.K_MASTER)
                .addKnob(initKnob(R.id.bass_knob, R.id.bass_text), Constants.K_BASS)
                .addKnob(initKnob(R.id.treble_knob, R.id.treble_text), Constants.K_TREB)
                .addKnob(initKnob(R.id.middle_knob, R.id.middle_text), Constants.K_MID)
                .addSpinner(ampMode, Constants.AMP)
                .addSpinner(cabMode, Constants.CAB)
    }


    override val fragmentId: Int
        get() = R.layout.amp

}