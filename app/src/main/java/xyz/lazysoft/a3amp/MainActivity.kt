package xyz.lazysoft.a3amp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.components.Dialogs
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AppDatabase
import xyz.lazysoft.a3amp.view.AbstractThrFragment
import xyz.lazysoft.a3amp.view.AmpReviewFragment
import javax.inject.Inject
import xyz.lazysoft.a3amp.amp.Constants.Companion as Const

class MainActivity : AppCompatActivity() {
    private val logger = AnkoLogger(Const.TAG)

    @Inject
    lateinit var thr: Amp

    @Inject
    lateinit var repository: AppDatabase

    private val topLevelFragments = listOf(
            AmpReviewFragment(),
            PresetsFragment())

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun initAmp() {

        // thr id detect
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

        fragments = AbstractThrFragment.listFragments()

        // drawer header
        val header = ImageView(this)
        header.image = getDrawable(R.drawable.header)
        header.scaleType = ImageView.ScaleType.CENTER

        val drawer = DrawerBuilder()
                .withHeader(header)
                .withActivity(this)
                .withToolbar(amp_toolbar)
                .withOnDrawerItemClickListener { _, _, drawerItem ->
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.container, fragments[drawerItem.identifier.toInt()])
                            .commit()
                    true
                }
                .withDrawerWidthDp(250)

        val drawerMenu = resources.getStringArray(R.array.tabs)
        IntRange(0, drawerMenu.size - 1).forEach {
            drawer.addDrawerItems(
                    PrimaryDrawerItem()
                            .withName(drawerMenu[it])
                            .withIdentifier(it.toLong()))
        }

        drawer.addDrawerItems(DividerDrawerItem())

        mainBottomNav.setOnNavigationItemSelectedListener { item ->
            replaceContext(item.itemId)
            false
        }

        drawer.build()
//        val tabs = resources.getStringArray(R.array.tabs).toList()
//        amp_pager.adapter = AmpPageAdapter(supportFragmentManager, tabs)
//        amp_pager.offscreenPageLimit = tabs.size
//
//        // val tabs = findViewById<DachshundTabLayout>(R.id.amp_tab)
//        amp_tab.setupWithViewPager(amp_pager)

        super.onCreate(savedInstanceState)

        replaceContext(R.id.mbm_amp)
    }

    private fun replaceContext(id: Int) {
        val f: Fragment? = when (id) {
            R.id.mbm_amp -> topLevelFragments[0]
            R.id.mbm_presets -> topLevelFragments[1]
            else -> null
        }

        f?.let { fragment ->
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit()
        }
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
            R.id.list_presets -> startActivity(Intent(this, PresetsFragment::class.java))
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

