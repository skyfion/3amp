package xyz.lazysoft.a3amp.view

import android.view.View
import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.amp.Constants

class ReverbFragment: AbstractThrFragment() {
    override val fragmentId: Int
        get() = R.layout.reverb

    override fun initFragment() {
//        thr.addOffSpinner(initCarousel(R.id.reverb_mode_carousel, R.array.reverbs
//        ) { value ->
//            sequenceOf(R.id.reverb_empty_block,
//                    R.id.common_reverb_block, R.id.spring_reverb_block)
//                    .map { fragmentView!!.findViewById<View>(it) }
//                    .withIndex().forEach { it ->
//                        val index = when (value) {
//                            in 1..3 -> 1
//                            4 -> 2
//                            else -> 0
//                        }
//                        it.value.visibility = if (index == it.index) View.VISIBLE else View.GONE
//                    }
//        },
//                Constants.REVERB_MODE, Constants.REVERB_SW)
//                .addKnob(initKnob(R.id.reverb_time_knob,
//                        R.id.reverb_time_text, Pair(3, 200)), Constants.REVERB_TIME)
//                .addKnob(initKnob(R.id.reverb_pre_delay_knob,
//                        R.id.reverb_pre_dalay_text, Pair(1, 2000)), Constants.REVERB_PRE_DELAY)
//                .addKnob(initKnob(R.id.reverb_low_cut,
//                        R.id.reverb_low_cut_text, Pair(21, 8000)), Constants.REVERB_LOW_CUT)
//                .addKnob(initKnob(R.id.reverb_hi_cut_knob,
//                        R.id.reverb_high_cut_text, Pair(1000, 16001)), Constants.REVERB_HIGH_CUT)
//                .addKnob(initKnob(R.id.reverb_hi_ratio_knob,
//                        R.id.reverb_high_ratio_text, Pair(1, 10)), Constants.REVERB_HIGH_RATIO)
//                .addKnob(initKnob(R.id.reverb_low_ratio_knob,
//                        R.id.reverb_low_ratio_text, Pair(1, 14)), Constants.REVERB_LOW_RATIO)
//                .addKnob(initKnob(R.id.reverb_level_knob,
//                        R.id.reverb_level_text), Constants.REVERB_LEVEL)
//                .addKnob(initKnob(R.id.reverb_spring_reverb,
//                        R.id.reverb_spring_reverb_text), Constants.REVERB_TIME)
//                .addKnob(initKnob(R.id.reverb_spring_filter_knob,
//                        R.id.reverb_spring_filter_text), Constants.REVERB_SPRING_FILTER)
    }
}