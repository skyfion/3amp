package xyz.lazysoft.a3amp

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.amp.Constants.Companion.READ_REQUEST_CODE
import xyz.lazysoft.a3amp.components.PresetAdapter
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AppDatabase
import java.io.File
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_presets, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.import_ydl -> {
                performFileSearch()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

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
                Log.i(ContentValues.TAG, "Uri: ${getPath(uri)}")
                val file = File(getPath(uri)).inputStream()

                Log.i(ContentValues.TAG, file.readBytes().size.toString())
            }


        }

    }

}
