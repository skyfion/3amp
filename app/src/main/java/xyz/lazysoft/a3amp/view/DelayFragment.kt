package xyz.lazysoft.a3amp.view

import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.amp.Constants

class DelayFragment : AbstractThrFragment() {
    override val fragmentId: Int
        get() = R.layout.delay

    override fun initFragment() {
        // todo only sw
        initCarousel(R.id.delay_sw_carousel, R.array.sw_modes,
                Constants.DELAY_SW, null,
                blockActivator(R.id.delay_empty_block, R.id.delay_block))
        initKnob(find(R.id.delay_feedback_knob),
                find(R.id.delay_feedback_text), Constants.DELAY_FEEDBACK)
        initKnob(find(R.id.delay_level_knob),
                find(R.id.delay_level_text), Constants.DELAY_LEVEL)
        initKnob(find(R.id.delay_time_knob),
                find(R.id.delay_time_text), Constants.DELAY_TIME, Pair(1, 9999))
        initKnob(find(R.id.delay_high_cut_knob),
                find(R.id.delay_high_cut_text), Constants.DELAY_HIGH_CUT, Pair(1000, 16001))
        initKnob(find(R.id.delay_low_cut_knob),
                find(R.id.delay_low_cut_text), Constants.DELAY_LOW_CUT, Pair(21, 8000))
    }
}