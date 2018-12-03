package xyz.lazysoft.a3amp

import `in`.goodiebag.carouselpicker.CarouselPicker
import android.app.Activity
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.TextView
import dagger.android.AndroidInjection
import dagger.android.DaggerApplication
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.amp.AmpModule
import xyz.lazysoft.a3amp.components.AmpComponent
import xyz.lazysoft.a3amp.components.wrappers.AmpCarouselWrapper
import xyz.lazysoft.a3amp.components.wrappers.AmpKnobWrapper
import xyz.lazysoft.a3amp.di.AppModule
import xyz.lazysoft.a3amp.di.DaggerAppComponent
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AppDatabase
import xyz.lazysoft.a3amp.persistence.RoomModule
import javax.inject.Inject
import xyz.lazysoft.a3amp.amp.Constants.Companion as Const

class MainActivity : AppCompatActivity() {
    private val log = AnkoLogger(Const.TAG)

    @Inject
    lateinit var thr: Amp

    @Inject
    lateinit var repository: AppDatabase

    private fun initKnob(knob: Int, text: Int): AmpComponent<Int> {
        return initKnob(knob, text, null)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun initKnob(knob: Int, text: Int, range: Pair<Int, Int>?): AmpComponent<Int> {
        val ampKnobWrapper = AmpKnobWrapper(findViewById(knob), range)
        val knobText = findViewById<TextView>(text)

        ampKnobWrapper.setOnStateChanged {
            knobText.text = it.toString()
        }
        return ampKnobWrapper
    }

    private fun blockActivator(vararg ids: Int): (value: Int) -> Unit {
        return { value ->
            ids.map { findViewById<View>(it) }
                    .withIndex()
                    .forEach { (index, view) -> view.visibility = if (index == value) VISIBLE else GONE }
        }
    }

    private fun initCarousel(carousel: Int, content: Int): AmpComponent<Int> {
        return initCarousel(carousel, content, null)
    }

    private fun initCarousel(carousel: Int, content: Int,
                             changeListener: ((mode: Int) -> Unit)?): AmpComponent<Int> {
        val carouselPicker = findViewById<CarouselPicker>(carousel)
        val textItems = resources.getStringArray(content)
                .map { CarouselPicker.TextItem(it, 10) }
        carouselPicker.adapter = CarouselPicker.CarouselViewAdapter(this, textItems, 0)
        val ampCarouselWrapper = AmpCarouselWrapper(carouselPicker)
        if (changeListener != null)
            ampCarouselWrapper.setOnStateChanged(changeListener)
        return ampCarouselWrapper
    }

    private fun initAmp() {
        initCarousel(R.id.tabs_carousel, R.array.tabs,
                blockActivator(R.id.tab_amp, R.id.tab_compressor,
                        R.id.tab_effects, R.id.tab_delay,
                        R.id.tab_reverb, R.id.tab_gate))

        thr.addKnob(initKnob(R.id.gain_knob, R.id.gain_text), Const.K_GAIN)
                .addKnob(initKnob(R.id.master_knob, R.id.master_text), Const.K_MASTER)
                .addKnob(initKnob(R.id.bass_knob, R.id.bass_text), Const.K_BASS)
                .addKnob(initKnob(R.id.treble_knob, R.id.treble_text), Const.K_TREB)
                .addKnob(initKnob(R.id.middle_knob, R.id.middle_text), Const.K_MID)
                .addSpinner(initCarousel(R.id.amp_carousel, R.array.thr10_amps), Const.AMP)
                .addSpinner(initCarousel(R.id.cab_carousel, R.array.thr10_cabs), Const.CAB)

        // amp model detect
        val ampNameText = findViewById<TextView>(R.id.amp_name)
        thr.modelAmpDetect = { s -> runOnUiThread { ampNameText.text = s } }

        // compressor
        thr.addKnob(
                initKnob(R.id.compressor_output_knob, R.id.c_output_text),
                Const.COMPRESSOR_STOMP_OUTPUT)
                .addKnob(initKnob(R.id.compressor_sustain_knob, R.id.c_sustain_text),
                        Const.COMPRESSOR_STOMP_SUSTAIN)
                .addOffSpinner(initCarousel(R.id.compressor_mode_carousel,
                        R.array.compressor_modes
                        , blockActivator(R.id.compressor_empty_block,
                        R.id.compressor_stomp, R.id.compressor_rack))
                        , Const.COMPRESSOR_MODE, Const.COMPRESSOR_SW)
                // rack
                .addKnob(initKnob(R.id.c_threshold_knob, R.id.c_rack_threshold_text, Pair(0, 600)),
                        Const.COMPRESSOR_RACK_THRESHOLD)
                .addKnob(initKnob(R.id.c_attack_knob, R.id.c_rack_attack_text),
                        Const.COMPRESSOR_RACK_ATTACK)
                .addKnob(initKnob(R.id.c_release_knob, R.id.c_release_text),
                        Const.COMPRESSOR_RACK_RELEASE)
                .addKnob(initKnob(R.id.c_rack_output_knob, R.id.c_rack_output_text, Pair(0, 600)),
                        Const.COMPRESSOR_RACK_OUTPUT)
                .addSpinner(initCarousel(R.id.compressor_knee_carousel, R.array.knee),
                        Const.COMPRESSOR_RACK_KNEE)
                .addSpinner(initCarousel(R.id.compressor_ratio_carousel, R.array.ratio),
                        Const.COMPRESSOR_RACK_RATIO)


        // effects
        thr.addOffSpinner(initCarousel(R.id.effect_mode_carousel, R.array.effects,
                blockActivator(
                        R.id.effect_empty_block,
                        R.id.effect_chorus_block,
                        R.id.effect_flanger_block,
                        R.id.effect_tremolo_block,
                        R.id.effect_phaser_block)), Const.EFFECTS_MODE, Const.EFFECTS_SW)
                // chorus
                .addKnob(initKnob(R.id.chorus_speed_knob,
                        R.id.chorus_speed_text), Const.EFFECT_KNOB1)
                .addKnob(initKnob(R.id.chorus_depth_knob,
                        R.id.chorus_depth_text), Const.EFFECT_KNOB2)
                .addKnob(initKnob(R.id.chorus_mix_knob,
                        R.id.chorus_mix_text), Const.EFFECT_KNOB3)
                // flanger
                .addKnob(initKnob(R.id.flanger_speed_knob,
                        R.id.flanger_speed_text), Const.EFFECT_KNOB1)
                .addKnob(initKnob(R.id.flanger_manual_knob,
                        R.id.flanger_manual_text), Const.EFFECT_KNOB2)
                .addKnob(initKnob(R.id.flanger_depth_knob,
                        R.id.flanger_depth_text), Const.EFFECT_KNOB3)
                .addKnob(initKnob(R.id.flanger_feedback_knob,
                        R.id.flanger_feedback_text), Const.EFFECT_KNOB4)
                .addKnob(initKnob(R.id.flanger_spread_knob,
                        R.id.flanger_spread_text), Const.EFFECT_KNOB5)
                // tremolo
                .addKnob(initKnob(R.id.tremolo_freq_knob,
                        R.id.tremolo_freq_text), Const.EFFECT_KNOB1)
                .addKnob(initKnob(R.id.tremolo_depth_knob,
                        R.id.tremolo_depth_text), Const.EFFECT_KNOB2)
                // phaser
                .addKnob(initKnob(R.id.phaser_speed_knob,
                        R.id.phaser_speed_text), Const.EFFECT_KNOB1)
                .addKnob(initKnob(R.id.phaser_manual_knob,
                        R.id.phaser_manual_text), Const.EFFECT_KNOB2)
                .addKnob(initKnob(R.id.phaser_depth_knob,
                        R.id.phaser_depth_text), Const.EFFECT_KNOB3)
                .addKnob(initKnob(R.id.phaser_feedback_knob,
                        R.id.phaser_feedback_text), Const.EFFECT_KNOB4)

        // delay
        thr.addSwSpinner(initCarousel(R.id.delay_sw_carousel, R.array.sw_modes,
                blockActivator(R.id.delay_empty_block,
                        R.id.delay_block)), Const.DELAY_SW)
                .addKnob(initKnob(R.id.delay_feedback_knob,
                        R.id.delay_feedback_text), Const.DELAY_FEEDBACK)
                .addKnob(initKnob(R.id.delay_level_knob,
                        R.id.delay_level_text), Const.DELAY_LEVEL)
                .addKnob(initKnob(R.id.delay_time_knob,
                        R.id.delay_time_text, Pair(1, 9999)), Const.DELAY_TIME)
                .addKnob(initKnob(R.id.delay_high_cut_knob,
                        R.id.delay_high_cut_text, Pair(1000, 16001)), Const.DELAY_HIGH_CUT)
                .addKnob(initKnob(R.id.delay_low_cut_knob,
                        R.id.delay_low_cut_text, Pair(21, 8000)), Const.DELAY_LOW_CUT)

        // reverb
        thr.addOffSpinner(initCarousel(R.id.reverb_mode_carousel, R.array.reverbs
        ) { value ->
            sequenceOf(R.id.reverb_empty_block,
                    R.id.common_reverb_block, R.id.spring_reverb_block)
                    .map { findViewById<View>(it) }
                    .withIndex().forEach { it ->
                        val index = when (value) {
                            in 1..3 -> 1
                            4 -> 2
                            else -> 0
                        }
                        it.value.visibility = if (index == it.index) VISIBLE else GONE
                    }
        },
                Const.REVERB_MODE, Const.REVERB_SW)
                .addKnob(initKnob(R.id.reverb_time_knob,
                        R.id.reverb_time_text, Pair(3, 200)), Const.REVERB_TIME)
                .addKnob(initKnob(R.id.reverb_pre_delay_knob,
                        R.id.reverb_pre_dalay_text, Pair(1, 2000)), Const.REVERB_PRE_DELAY)
                .addKnob(initKnob(R.id.reverb_low_cut,
                        R.id.reverb_low_cut_text, Pair(21, 8000)), Const.REVERB_LOW_CUT)
                .addKnob(initKnob(R.id.reverb_hi_cut_knob,
                        R.id.reverb_high_cut_text, Pair(1000, 16001)), Const.REVERB_HIGH_CUT)
                .addKnob(initKnob(R.id.reverb_hi_ratio_knob,
                        R.id.reverb_high_ratio_text, Pair(1, 10)), Const.REVERB_HIGH_RATIO)
                .addKnob(initKnob(R.id.reverb_low_ratio_knob,
                        R.id.reverb_low_ratio_text, Pair(1, 14)), Const.REVERB_LOW_RATIO)
                .addKnob(initKnob(R.id.reverb_level_knob,
                        R.id.reverb_level_text), Const.REVERB_LEVEL)
                .addKnob(initKnob(R.id.reverb_spring_reverb,
                        R.id.reverb_spring_reverb_text), Const.REVERB_TIME)
                .addKnob(initKnob(R.id.reverb_spring_filter_knob,
                        R.id.reverb_spring_filter_text), Const.REVERB_FILTER)
        // gate
        thr.addSwSpinner(initCarousel(R.id.gate_carousel, R.array.sw_modes,
                blockActivator(R.id.gate_empty_block, R.id.gate_block)), Const.GATE_SW)
                .addKnob(initKnob(R.id.gate_release_knob,
                        R.id.gate_release_text), Const.GATE_RELEASE)
                .addKnob(initKnob(R.id.gate_threshold_knob,
                        R.id.gate_threshold_text), Const.GATE_THRESHOLD)

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        (application as AmpApplication).component.inject(this)
        setContentView(R.layout.activity_main)

        initAmp()
        setSupportActionBar(findViewById(R.id.amp_toolbar))

    }

    private val READ_REQUEST_CODE: Int = 42

    fun performFileSearch() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            // Filter to only show results that can be "opened", such as a
            // file (as opposed to a list of contacts or timezones)
            addCategory(Intent.CATEGORY_OPENABLE)

            // Filter to show only images, using the image MIME data type.
            // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
            // To search for all documents available via installed storage providers,
            // it would be "*/*".
            type = "*/*"
        }

        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            resultData?.data?.also { uri ->
                Log.i(ContentValues.TAG, "Uri: $uri")

            }
        }
    }

    private fun savePresetAsDialog(): Boolean {
        lateinit var dialog: DialogInterface
        lateinit var presetName: EditText
        dialog = alert {
            title = "Save as .."
            isCancelable = true
            customView {
                verticalLayout() {
                    linearLayout() {
                       presetName = editText {
                            hint = "enter name"
                        }.lparams(width = matchParent)
                    }.lparams(width = matchParent)
                    linearLayout() {
                        button("Cancel") {
                            onClick {
                                dialog.dismiss()
                            }
                        }
                        button("OK") {
                            onClick {
                                val presetTitle = presetName.text.toString()
                                if (TextUtils.isEmpty(presetTitle)) {
                                    presetName.error = "Enter name!"
                                } else {
                                    val preset = AmpPreset(
                                            title = presetTitle,
                                            dump = thr.dumpState.toByteArray())
                                    doAsync {
                                        repository.presetDao().insert(preset)
                                    }
                                    dialog.dismiss()
                                }
                            }
                        }
                    }.lparams(width = matchParent)
                }
            }

        }.show()
        return true
    }

    private fun savePreset(): Boolean {
        thr.selectPreset?.let {preset ->
            doAsync {
                preset.dump = thr.dumpState.toByteArray()
                repository.presetDao().update(preset)
            }
        }?: run {
            savePresetAsDialog()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.save_preset_as -> savePresetAsDialog()
            R.id.save_preset -> savePreset()
            R.id.list_presets -> {
                startActivity(Intent(this, PresetsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}

