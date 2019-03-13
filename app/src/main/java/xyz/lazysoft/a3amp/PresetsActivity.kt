package xyz.lazysoft.a3amp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_presets.*
import org.jetbrains.anko.*
import xyz.lazysoft.a3amp.amp.*
import xyz.lazysoft.a3amp.amp.Constants.Companion.READ_REQUEST_CODE
import xyz.lazysoft.a3amp.components.Dialogs
import xyz.lazysoft.a3amp.components.presets.PresetExpandableListAdapter
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AmpPresetGroup
import xyz.lazysoft.a3amp.persistence.AppDatabase
import javax.inject.Inject


class PresetsActivity : AppCompatActivity() {

    @Inject
    lateinit var amp: Amp

    @Inject
    lateinit var repository: AppDatabase

    var presets: List<AmpPreset>? = null

    private lateinit var presetList: ExpandableListView

    private lateinit var listAdapter: PresetExpandableListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as AmpApplication).component.inject(this)
        setContentView(R.layout.activity_presets)
        val toolbar = findViewById<Toolbar>(R.id.preset_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        presetList = findViewById(R.id.presets_item_list)
        listAdapter = PresetExpandableListAdapter(this, repository.presetDao())
        presetList.setAdapter(listAdapter)


        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.rename_preset_or_group -> {
                    val obj = presetList.selectedItem
                    if (obj != null) {
                        Dialogs.showInputDialog(this, "Rename", item.title.toString())
                        { title ->
                            when (obj) {
                                is AmpPreset -> renamePreset(obj, title)
                                is AmpPresetGroup -> renameGroup(obj, title)
                            }
                        }
                    }
                }
                R.id.delete_preset_or_group -> {
                    val obj = presetList.selectedItem
                    if (obj != null) {
                        alert("${getString(R.string.common_delete_dialog)} ${item.title}?") {
                            yesButton {
                                deleteItem(obj)
                            }
                            noButton {  }
                        }.show()
                    }

                }
            }
            true
        }

    }

    private fun deleteItem(item: Any) {
        doAsync {
            when (item) {
                is AmpPreset -> repository.presetDao().delete(item)
                is AmpPresetGroup -> repository.presetDao().deleteGroup(item)
            }
        }
    }

    private fun renameGroup(item: AmpPresetGroup, title: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun renamePreset(item: AmpPreset, title: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        (presetList.adapter as PresetExpandableListAdapter).refresh()
    }


}

