package xyz.lazysoft.a3amp

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.jetbrains.anko.*
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.amp.Constants
import xyz.lazysoft.a3amp.amp.Constants.Companion.READ_REQUEST_CODE
import xyz.lazysoft.a3amp.amp.Utils
import xyz.lazysoft.a3amp.amp.YdlFile
import xyz.lazysoft.a3amp.components.Dialogs
import xyz.lazysoft.a3amp.components.presets.PresetExpandableListAdapter
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AmpPresetGroup
import xyz.lazysoft.a3amp.persistence.AppDatabase
import javax.inject.Inject


class PresetsActivity : AppCompatActivity() {

    private val logger = AnkoLogger(Constants.TAG)

    @Inject
    lateinit var amp: Amp

    @Inject
    lateinit var repository: AppDatabase

    var presets: List<AmpPreset>? = null
    var selected: Any? = null

    private lateinit var toolbar: Toolbar

    private lateinit var presetList: ExpandableListView

    private lateinit var listAdapter: PresetExpandableListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as AmpApplication).component.inject(this)
        setContentView(R.layout.activity_presets)
        toolbar = findViewById(R.id.preset_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        presetList = findViewById(R.id.presets_item_list)
        listAdapter = PresetExpandableListAdapter(this, repository.presetDao())
        presetList.setAdapter(listAdapter)

        updateToolbarTitle()

        val loadToolbarBtn = findViewById<View>(R.id.load_preset)
        val renameToolbarBtn = findViewById<View>(R.id.rename_preset_or_group)
        val deleteToolbarBtn = findViewById<View>(R.id.delete_preset_or_group)

        loadToolbarBtn.isEnabled = false
        renameToolbarBtn.isEnabled = false
        deleteToolbarBtn.isEnabled = false

        presetList.setOnChildClickListener { expandableListView, view, groupPos, childPos, id ->
            loadToolbarBtn.isEnabled = true
            renameToolbarBtn.isEnabled = true
            deleteToolbarBtn.isEnabled = true

            listAdapter.setSelection(childPos, groupPos)

            selected = listAdapter.getChild(groupPos, childPos)
            true
        }

        presetList.setOnGroupClickListener { expandableListView, view, groupPos, id ->
            if (expandableListView.isGroupExpanded(groupPos)) {
                expandableListView.collapseGroup(groupPos)
            } else {
                expandableListView.expandGroup(groupPos)
            }

            listAdapter.setSelection(PresetExpandableListAdapter.NOT_SELECTED, groupPos)
            selected = listAdapter.getGroup(groupPos)

            if ((selected as AmpPresetGroup).uid != null) {
                renameToolbarBtn.isEnabled = true
                deleteToolbarBtn.isEnabled = true
            } else {
                renameToolbarBtn.isEnabled = false
                deleteToolbarBtn.isEnabled = false
            }

            loadToolbarBtn.isEnabled = false

            true
        }

        presetList.setOnItemLongClickListener { adapterView, view, pos, id ->
            logger.info("long click !")
            if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(null, View.DragShadowBuilder(view), adapterView.getItemAtPosition(pos), 0)
                } else {
                    view.startDrag(null, View.DragShadowBuilder(view), adapterView.getItemAtPosition(pos), 0)
                }

            }
            false
        }

        presetList.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val pos = presetList.pointToPosition(event.x.toInt(), event.y.toInt())
                    val item = presetList.getItemAtPosition(pos)
                    if (item != null && event.localState is AmpPreset) {
                        val group = when (item) {
                            is AmpPresetGroup -> item.uid
                            is AmpPreset -> item.group
                            else -> null
                        }
                        val preset = event.localState as AmpPreset
                        if (preset.group != group) {
                            doAsync {
                                logger.debug("drag -> update group preset")
                                repository.presetDao().update(preset.copy(group = group))
                                onComplete {
                                    listAdapter.refresh()
                                }
                            }
                        }
                    }
                }
            }
            true
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.load_preset -> {
                    val obj = selected
                    when (obj) {
                        is AmpPreset -> {
                            amp.selectPreset = obj
                            updateToolbarTitle()
                        }
                    }
                }
                R.id.create_preset_group -> {
                    Dialogs.showInputDialog(this, "New group", "") { groupTitle ->
                        newGroup(groupTitle)
                    }
                }
                R.id.rename_preset_or_group -> {
                    val obj = selected
                    if (obj != null) {
                        Dialogs.showInputDialog(this, "Rename ", obj.toString())
                        { title ->
                            when (obj) {
                                is AmpPreset -> renamePreset(obj, title)
                                is AmpPresetGroup -> renameGroup(obj, title)
                            }
                        }
                    } else {
                        logger.info("obj is null")
                    }
                }
                R.id.delete_preset_or_group -> {
                    val obj = selected
                    if (obj != null) {
                        alert("${getString(R.string.common_delete_dialog)} $obj?") {
                            yesButton {
                                deleteItem(obj)
                            }
                            noButton { }
                        }.show()
                    }

                }
            }
            true
        }

    }

    private fun updateToolbarTitle() {
        amp.selectPreset?.let {
            toolbar.title = "${getString(R.string.presets_toolbar_title)} - ${it.title}"
        }
    }

    private fun deleteItem(item: Any) {
        doAsync {
            when (item) {
                is AmpPreset -> repository.presetDao().delete(item)
                is AmpPresetGroup -> repository.presetDao().deleteGroup(item)
            }
            onComplete {
                listAdapter.refresh()
            }
        }
    }

    private fun renameGroup(item: AmpPresetGroup, title: String) {
        doAsync {
            repository.presetDao().updateGroup(item.copy(title = title))
            onComplete {
                listAdapter.refresh()
            }
        }
    }

    private fun renamePreset(item: AmpPreset, title: String) {
        doAsync {
            repository.presetDao().update(item.copy(title = title))
            onComplete {
                listAdapter.refresh()
            }
        }
    }

    private fun newGroup(title: String) {
        doAsync {
            repository.presetDao().insertGroup(AmpPresetGroup(title = title))
            onComplete {
                listAdapter.refresh()
            }
        }
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

    private fun performFileSearch() {
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
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.let { uri ->
                logger.debug("start import ydl")
                val fileName = Utils.getFileName(this, uri)
                contentResolver.openInputStream(uri)?.let { inputStream ->
                    val ydl = YdlFile(inputStream)
                    val err = ydl.errorReason()
                    if (err != null) {
                        alert(err) { okButton {} }.show()
                    } else {
                        ydl.presetData()
                                ?.filter { !it.isInit() }
                                ?.let { presets ->
                                    Dialogs.showInputDialog(this,
                                            getString(R.string.enter_a_preset_name),
                                            fileName) { groupName ->
                                        doAsync {

                                            val groupId = repository.presetDao().insertGroup(AmpPresetGroup(title = groupName)).toInt()

                                            presets.forEach {
                                                repository.presetDao().insert(
                                                        AmpPreset(title = it.name,
                                                                dump = it.data,
                                                                model = it.model?.id,
                                                                group = groupId))
                                            }
                                            onComplete {
                                                listAdapter.refresh()
                                            }
                                        }

                                    }

                                }
                    }
                }
            }
        }

    }

}

