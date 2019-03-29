package xyz.lazysoft.a3amp.view

import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.amp.Constants

class EffectsFragment: AbstractThrFragment() {
    override val fragmentId: Int
        get() = R.layout.effects

    override fun initFragment() {
        thr.addOffSpinner(initCarousel(R.id.effect_mode_carousel, R.array.effects,
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
    }
}