package xyz.lazysoft.a3amp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.components.Dialogs
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AppDatabase
import xyz.lazysoft.a3amp.view.AbstractThrFragment
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

    private lateinit var fragments: List<AbstractThrFragment>

    override fun onCreate(savedInstanceState: Bundle?) {

        (application as AmpApplication).component.inject(this)
        setContentView(R.layout.activity_main)

        setSupportActionBar(amp_toolbar)
//        val actionbar: ActionBar? = supportActionBar
//        actionbar?.apply {
//            setDisplayHomeAsUpEnabled(true)
//            setHomeAsUpIndicator(R.drawable.ic_menu)
//        }

        fragments = AbstractThrFragment.listFragments()

        val drawer = DrawerBuilder()
                .withToolbar(amp_toolbar)
                .withOnDrawerItemClickListener { view, position, drawerItem ->
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.container, fragments[drawerItem.identifier.toInt()])
                            .commit()
                    true
                }
                .withActivity(this)

        val drawerMenu = resources.getStringArray(R.array.tabs)
        IntRange(0, drawerMenu.size - 1).forEach {
            drawer.addDrawerItems(
                    PrimaryDrawerItem().withName(drawerMenu[it]).withIdentifier(it.toLong())
            )
        }

        drawer.addDrawerItems(DividerDrawerItem())


        drawer.build()
//        val tabs = resources.getStringArray(R.array.tabs).toList()
//        amp_pager.adapter = AmpPageAdapter(supportFragmentManager, tabs)
//        amp_pager.offscreenPageLimit = tabs.size
//
//        // val tabs = findViewById<DachshundTabLayout>(R.id.amp_tab)
//        amp_tab.setupWithViewPager(amp_pager)
        super.onCreate(savedInstanceState)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragments[0])
                .commit()

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
            // R.id.home -> drawer_layout.openDrawer(GravityCompat.START)
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

