package xyz.lazysoft.a3amp.view

import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.amp.Constants

class CompressorFragment: AbstractThrFragment() {
    override val fragmentId: Int
        get() = R.layout.compressor

    override fun initFragment() {
//        thr.addKnob(
//                initKnob(R.id.compressor_output_knob, R.id.c_output_text),
//                Constants.COMPRESSOR_STOMP_OUTPUT)
//                .addKnob(initKnob(R.id.compressor_sustain_knob, R.id.c_sustain_text),
//                        Constants.COMPRESSOR_STOMP_SUSTAIN)
//                .addOffSpinner(initCarousel(R.id.compressor_mode_carousel,
//                        R.array.compressor_modes
//                        , blockActivator(R.id.compressor_empty_block,
//                        R.id.compressor_stomp, R.id.compressor_rack))
//                        , Constants.COMPRESSOR_MODE, Constants.COMPRESSOR_SW)
//                // rack
//                .addKnob(initKnob(R.id.c_threshold_knob, R.id.c_rack_threshold_text, Pair(0, 600)),
//                        Constants.COMPRESSOR_RACK_THRESHOLD)
//                .addKnob(initKnob(R.id.c_attack_knob, R.id.c_rack_attack_text),
//                        Constants.COMPRESSOR_RACK_ATTACK)
//                .addKnob(initKnob(R.id.c_release_knob, R.id.c_release_text),
//                        Constants.COMPRESSOR_RACK_RELEASE)
//                .addKnob(initKnob(R.id.c_rack_output_knob, R.id.c_rack_output_text, Pair(0, 600)),
//                        Constants.COMPRESSOR_RACK_OUTPUT)
//                .addSpinner(initCarousel(R.id.compressor_knee_carousel, R.array.knee),
//                        Constants.COMPRESSOR_RACK_KNEE)
//                .addSpinner(initCarousel(R.id.compressor_ratio_carousel, R.array.ratio),
//                        Constants.COMPRESSOR_RACK_RATIO)
    }


}