package xyz.lazysoft.a3amp

import `in`.goodiebag.carouselpicker.CarouselPicker
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import it.beppi.knoblibrary.Knob
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.amp.AmpModel
import xyz.lazysoft.a3amp.components.wrappers.AmpComponent
import xyz.lazysoft.a3amp.components.Dialogs
import xyz.lazysoft.a3amp.components.wrappers.AmpCarouselWrapper
import xyz.lazysoft.a3amp.components.wrappers.AmpKnobWrapper
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AppDatabase
import javax.inject.Inject
import xyz.lazysoft.a3amp.amp.Constants.Companion as Const


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key != null && key == SettingsActivity.KNOB_MODE) {
            setKnobMode(sharedPreferences?.getString(key, Knob.SWIPEDIRECTION_CIRCULAR.toString()))
        }
    }

    private fun setKnobMode(mode: String?) {
        mode?.let {
            knobs.map { k -> k.knob.swipeDirection = it.toInt() }
        }
    }

    private fun initSettings() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        setKnobMode(pref.getString(SettingsActivity.KNOB_MODE,
                Knob.SWIPEDIRECTION_CIRCULAR.toString()))
    }

    private var sharedPref: SharedPreferences? = null

    private val logger = AnkoLogger(Const.TAG)

    @Inject
    lateinit var thr: Amp

    @Inject
    lateinit var repository: AppDatabase

    val knobs = mutableListOf<AmpKnobWrapper>()

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
        knobs.add(ampKnobWrapper)
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
        val ampCarouselWrapper = AmpCarouselWrapper(carouselPicker)
        ampCarouselWrapper.setContent(content, this)
        if (changeListener != null)
            ampCarouselWrapper.setOnStateChanged(changeListener)
        return ampCarouselWrapper
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        (application as AmpApplication).component.inject(this)
        setContentView(R.layout.activity_main)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPref?.registerOnSharedPreferenceChangeListener(this)
        initSettings()
        setSupportActionBar(findViewById(R.id.amp_toolbar))
    }

    private fun savePreset() {
        thr.selectPreset?.let { preset ->
            doAsync {
                preset.dump = thr.dumpState.dump
                repository.presetDao().update(preset)
            }
        } ?: run {
            saveAs()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.save_preset_as -> saveAs()
            R.id.save_preset -> savePreset()
            R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.list_presets -> startActivity(Intent(this, PresetsActivity::class.java))
            R.id.about_btn -> startActivity(Intent(this, AboutActivity::class.java))
        }
        return true
    }

    private fun saveAs() {
        Dialogs.showInputDialog(this, getString(R.string.save_preset_as), "")
        {
            val preset = AmpPreset(
                    title = it,
                    dump = thr.dumpState.dump)
            doAsync {
                val id = repository.presetDao().insert(preset)
                onComplete {
                    thr.selectPreset = preset.copy(uid = id.toInt())
                }
            }
        }
    }

}

