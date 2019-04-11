package xyz.lazysoft.a3amp

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.alert
import xyz.lazysoft.a3amp.amp.Constants.Companion.READ_REQUEST_CODE
import xyz.lazysoft.a3amp.amp.Utils
import xyz.lazysoft.a3amp.amp.YdlFile
import xyz.lazysoft.a3amp.components.Dialogs
import xyz.lazysoft.a3amp.components.presets.PresetExpandableListAdapter
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AmpPresetGroup
import xyz.lazysoft.a3amp.view.AbstractThrFragment

class PresetsFragment : AbstractThrFragment() {

    var presets: List<AmpPreset>? = null

    private var selected: Any? = null

    private lateinit var presetList: ExpandableListView

    private lateinit var listAdapter: PresetExpandableListAdapter

    override val fragmentId: Int
        get() = R.layout.presets

    override fun initFragment() {
        presetList = fragmentView!!.findViewById(R.id.presets_item_list)
        listAdapter = PresetExpandableListAdapter(fragmentView!!.context, repository.presetDao())
        presetList.setAdapter(listAdapter)

        initToolBar()
        initDragAndDrop()
        initNavigation()
    }

    private fun initDragAndDrop() {
        presetList.setOnItemLongClickListener { adapterView, view, pos, id ->
            if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(null,
                            View.DragShadowBuilder(view),
                            adapterView.getItemAtPosition(pos), 0)
                } else {
                    view.startDrag(null,
                            View.DragShadowBuilder(view),
                            adapterView.getItemAtPosition(pos), 0)
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
    }

    private fun initToolBar() {
        val loadToolbarBtn = fragmentView!!.findViewById<View>(R.id.load_preset)
        val renameToolbarBtn = fragmentView!!.findViewById<View>(R.id.rename_preset_or_group)
        val deleteToolbarBtn = fragmentView!!.findViewById<View>(R.id.delete_preset_or_group)

        loadToolbarBtn.isEnabled = false
        renameToolbarBtn.isEnabled = false
        deleteToolbarBtn.isEnabled = false

        presetList.setOnChildClickListener { _, _, groupPos, childPos, _ ->
            loadToolbarBtn.isEnabled = true
            renameToolbarBtn.isEnabled = true
            deleteToolbarBtn.isEnabled = true

            listAdapter.setSelection(childPos, groupPos)

            selected = listAdapter.getChild(groupPos, childPos)
            true
        }

        presetList.setOnGroupClickListener { expandableListView, _, groupPos, _ ->
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
    }

    private fun initNavigation() {
        val bottomNavigation = fragmentView!!.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            val ctx = fragmentView!!.context
            when (item.itemId) {
                R.id.load_preset -> {
                    val obj = selected
                    when (obj) {
                        is AmpPreset -> {
                            thr.selectPreset = obj
                        }
                    }
                }
                R.id.create_preset_group -> {
                    Dialogs.showInputDialog(ctx, "New group", "")
                    { groupTitle ->
                        newGroup(groupTitle)
                    }
                }
                R.id.rename_preset_or_group -> {
                    val obj = selected
                    if (obj != null) {
                        Dialogs.showInputDialog(ctx, "Rename ", obj.toString())
                        { title ->
                            when (obj) {
                                is AmpPreset -> renamePreset(obj, title)
                                is AmpPresetGroup -> renameGroup(obj, title)
                            }
                        }
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
                R.id.import_presets -> {
                    performFileSearch()
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
            onComplete {
                listAdapter.refresh()
            }
        }
    }

    private fun normalizeTitle(text: String): String {
        return text.replace("\n", "")
                .replace("\n", "").trim()
    }

    private fun renameGroup(item: AmpPresetGroup, title: String) {
        doAsync {
            repository.presetDao().updateGroup(item.copy(title = normalizeTitle(title)))
            onComplete {
                listAdapter.refresh()
            }
        }
    }

    private fun renamePreset(item: AmpPreset, title: String) {
        doAsync {
            repository.presetDao().update(item.copy(title = normalizeTitle(title)))
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
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val context = fragmentView!!.context
            resultData?.data?.let { uri ->
                val fileName = Utils.getFileName(context, uri)
                context.contentResolver.openInputStream(uri)?.let { inputStream ->
                    val ydl = YdlFile(inputStream)
                    val err = ydl.errorReason()
                    if (err != null) {
                        alert(err) { okButton {} }.show()
                    } else {
                        ydl.presetData()
                                ?.filter { !it.isInit() }
                                ?.let { presets ->
                                    Dialogs.showInputDialog(
                                            context,
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { restoreState(it) }
    }

    private fun saveState(outState: Bundle) {
            val groupsCount = presetList.expandableListAdapter.groupCount
            val groupExpandedArray = BooleanArray(groupsCount)
            var i = 0
            while (i < groupsCount) {
                groupExpandedArray[i] = presetList.isGroupExpanded(i)
                i += 1
            }
            outState.putBooleanArray("groupExpandedArray", groupExpandedArray)
            outState.putInt("firstVisiblePosition", presetList.firstVisiblePosition)
    }

    private fun restoreState(savedInstanceState: Bundle) {
        val groupExpandedArray = savedInstanceState.getBooleanArray("groupExpandedArray")
        val firstVisiblePosition = savedInstanceState.getInt("firstVisiblePosition", -1)
        if (groupExpandedArray != null) {
            for (i in groupExpandedArray.indices) {
                if (groupExpandedArray[i])
                    presetList.expandGroup(i)
            }
            if (firstVisiblePosition >= 0)
                presetList.setSelection(firstVisiblePosition)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}

