package xyz.lazysoft.a3amp.view

import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.amp.Constants

class DelayFragment: AbstractThrFragment() {
    override val fragmentId: Int
        get() = R.layout.delay

    override fun initFragment() {
        thr.addSwSpinner(initCarousel(R.id.delay_sw_carousel, R.array.sw_modes,
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
    }
}