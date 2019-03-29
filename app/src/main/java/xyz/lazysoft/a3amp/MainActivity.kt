package xyz.lazysoft.a3amp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.components.Dialogs
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AppDatabase
import xyz.lazysoft.a3amp.view.AmpPageAdapter
import javax.inject.Inject
import xyz.lazysoft.a3amp.amp.Constants.Companion as Const

class MainActivity : AppCompatActivity() {
    private val logger = AnkoLogger(Const.TAG)

    @Inject
    lateinit var thr: Amp

    @Inject
    lateinit var repository: AppDatabase

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun initAmp() {

        // amp id detect
//        thr.modelAmpDetect = { model ->
//            runOnUiThread {
//
//                val thrMode: Int? = when (model) {
//                    AmpModel.THR10X -> R.array.thr10x_amps
//                    AmpModel.THR10 -> R.array.thr10_amps
//                    AmpModel.THR10C -> R.array.thr10c_amps
//                    AmpModel.THR5 -> R.array.thr5_amps
//                    AmpModel.THR5A -> R.array.thr5_amps
//                }
//
//                thrMode?.let {
//                    (ampMode as AmpCarouselWrapper).setContent(it, this)
//                }
//
//                val toolbar = findViewById<Toolbar>(R.id.amp_toolbar)
//                if (toolbar != null) {
//                    toolbar.title = "${resources.getString(R.string.app_name)} - ${model.name}"
//                }
//            }
//        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        (application as AmpApplication).component.inject(this)
        setContentView(R.layout.activity_main)


        setSupportActionBar(findViewById(R.id.amp_toolbar))

        amp_pager.adapter = AmpPageAdapter(
                supportFragmentManager,
                resources.getStringArray(R.array.tabs).toList())
        // val tabs = findViewById<DachshundTabLayout>(R.id.amp_tab)
        amp_tab.setViewPager(amp_pager)
        super.onCreate(savedInstanceState)

        thr.open()
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

