package xyz.lazysoft.a3amp.view

import kotlinx.android.synthetic.main.compressor.*
import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.amp.Constants

class CompressorFragment : AbstractThrFragment() {
    override val fragmentId: Int
        get() = R.layout.compressor

    override fun initFragment() {

//        initKnob(compressor_output_knob, c_output_text, Constants.COMPRESSOR_STOMP_OUTPUT)
//        initKnob(compressor_sustain_knob, c_sustain_text, Constants.COMPRESSOR_STOMP_SUSTAIN)
////        initCarousel(R.id.compressor_mode_carousel, R.array.compressor_modes,
////                Constants.COMPRESSOR_MODE, Constants.COMPRESSOR_SW,
////                blockActivator(R.id.compressor_empty_block, R.id.compressor_stomp, R.id.compressor_rack))
//        // rack
//        initKnob(c_threshold_knob, c_rack_threshold_text,
//                Constants.COMPRESSOR_RACK_THRESHOLD, Pair(0, 600))
//        initKnob(c_attack_knob, c_rack_attack_text, Constants.COMPRESSOR_RACK_ATTACK)
//        initKnob(c_release_knob, c_release_text, Constants.COMPRESSOR_RACK_RELEASE)
//        initKnob(c_rack_output_knob, c_rack_output_text,
//                Constants.COMPRESSOR_RACK_OUTPUT, Pair(0, 600))
//        initCarousel(R.id.compressor_knee_carousel, R.array.knee, Constants.COMPRESSOR_RACK_KNEE)
//        initCarousel(R.id.compressor_ratio_carousel, R.array.ratio, Constants.COMPRESSOR_RACK_RATIO)
    }


}