package xyz.lazysoft.a3amp.view

import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.amp.Constants

class GateFragment: AbstractThrFragment() {
    override val fragmentId: Int
        get() = R.layout.gate

    override fun initFragment() {
//        thr.addSwSpinner(initCarousel(R.id.gate_carousel, R.array.sw_modes,
//                blockActivator(R.id.gate_empty_block, R.id.gate_block)), Constants.GATE_SW)
//                .addKnob(initKnob(R.id.gate_release_knob,
//                        R.id.gate_release_text), Constants.GATE_RELEASE)
//                .addKnob(initKnob(R.id.gate_threshold_knob,
//                        R.id.gate_threshold_text), Constants.GATE_THRESHOLD)
    }
}