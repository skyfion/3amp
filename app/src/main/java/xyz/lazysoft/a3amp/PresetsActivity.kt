package xyz.lazysoft.a3amp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.components.PresetAdapter
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AppDatabase
import javax.inject.Inject


class PresetsActivity : AppCompatActivity() {

    var presets: List<AmpPreset>? = null
    private lateinit var presetsList: RecyclerView

    @Inject
    lateinit var repository: AppDatabase

    @Inject
    lateinit var amp: Amp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as AmpApplication).component.inject(this)
        setContentView(R.layout.activity_presets)
        val toolbar = findViewById<Toolbar>(R.id.preset_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        presetsList = findViewById(R.id.presets_item_list)
        initListPresets()

    }

    private fun initListPresets() {
        presetsList.layoutManager = LinearLayoutManager(this)
        presetsList.adapter =
                PresetAdapter(this, repository.presetDao(), amp)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.presets_menu, menu)
//        return true
//    }

}
