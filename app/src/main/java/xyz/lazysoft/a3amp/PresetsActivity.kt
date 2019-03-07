package xyz.lazysoft.a3amp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import xyz.lazysoft.a3amp.amp.*
import xyz.lazysoft.a3amp.amp.Constants.Companion.READ_REQUEST_CODE
import xyz.lazysoft.a3amp.components.PresetAdapter
import xyz.lazysoft.a3amp.components.presets.PresetExpandableListAdapter
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AmpPresetGroup
import xyz.lazysoft.a3amp.persistence.AppDatabase
import javax.inject.Inject


class PresetsActivity : AppCompatActivity() {

    var presets: List<AmpPreset>? = null
//    private lateinit var presetsList: RecyclerView

    private lateinit var presetList: ExpandableListView

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

        initListPresets()

    }

    private fun initListPresets() {
        presetList = findViewById(R.id.presets_item_list)
        val result = HashMap<String, List<String>>()
        doAsync {
            val groups = repository.presetDao().getAllGroups()
            val presets = repository.presetDao().getAll()
                    .groupBy { it.group }
            presets.keys.forEach { key ->
                val title = groups.find { g -> g.uid == key }?.title ?: "Custom"
                result[title] = presets.getValue(key).map { p -> p.title }
            }
            onComplete {
                presetList.setAdapter(
                        PresetExpandableListAdapter(
                                it!!,
                                result.keys.toList(),
                                result
                        ))
            }
        }

//        presetsList = findViewById(R.id.presets_item_list)
//        presetsList.layoutManager = LinearLayoutManager(this)
//        presetsList.adapter =
//                PresetAdapter(this, repository.presetDao(), amp)
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
                contentResolver.openInputStream(uri)?.let { inputStream ->
                    val ydl = YdlFile(inputStream)
                    ydl.presetData()
                            ?.filter { !it.isInit() }
                            ?.let { presets ->
                                doAsync {
                                    val groupName = "test"
                                    val groupId = repository.presetDao().insertGroup(AmpPresetGroup(title = groupName)).toInt()

                                    presets.forEach {
                                        repository.presetDao().insert(
                                                AmpPreset(title = it.name,
                                                        dump = it.data,
                                                        model = it.model?.id,
                                                        group = groupId))
                                    }
                                    onComplete {
                                        refreshList()
                                    }
                                }

                            }
                }
            }
        }

    }

    private fun refreshList() {
//        (it?.presetsList?.adapter as PresetAdapter).refresh()
    }


}
