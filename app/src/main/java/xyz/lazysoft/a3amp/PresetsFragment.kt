package xyz.lazysoft.a3amp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.alert
import xyz.lazysoft.a3amp.amp.Constants
import xyz.lazysoft.a3amp.amp.Utils
import xyz.lazysoft.a3amp.amp.YdFile
import xyz.lazysoft.a3amp.components.Dialogs
import xyz.lazysoft.a3amp.components.presets.PresetExpandableListAdapter
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AmpPresetGroup


class PresetsFragment : Fragment() {

    private lateinit var viewModel: PresetsViewModel
    private lateinit var presetsList: ExpandableListView
    private lateinit var listAdapter: PresetExpandableListAdapter

    companion object {
        fun newInstance() = PresetsFragment()
    }

    private fun <T : View> findViewById(id: Int): T {
       return view!!.findViewById(id)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.presets_fragment, container, false)

        presetsList = findViewById(R.id.presets_item_list)

        return inflate
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PresetsViewModel::class.java)

        listAdapter = PresetExpandableListAdapter(activity!!, viewModel.groups,
                viewModel.presets)

        presetsList.setAdapter(listAdapter)

        initFragment()
    }


    private fun initFragment() {
        val loadToolbarBtn =   findViewById<View>(R.id.load_preset)
        val renameToolbarBtn = findViewById<View>(R.id.rename_preset_or_group)
        val deleteToolbarBtn = findViewById<View>(R.id.delete_preset_or_group)

        loadToolbarBtn.isEnabled = false
        renameToolbarBtn.isEnabled = false
        deleteToolbarBtn.isEnabled = false

        presetsList.setOnChildClickListener { _, _, groupPos,
                                             childPos, _ ->
            loadToolbarBtn.isEnabled = true
            renameToolbarBtn.isEnabled = true
            deleteToolbarBtn.isEnabled = true

            listAdapter.setSelection(childPos, groupPos)

            viewModel.selected = listAdapter.getChild(groupPos, childPos)
            true
        }

        presetsList.setOnGroupClickListener { expandableListView, _,
                                             groupPos, _ ->
            if (expandableListView.isGroupExpanded(groupPos)) {
                expandableListView.collapseGroup(groupPos)
            } else {
                expandableListView.expandGroup(groupPos)
            }

            listAdapter.setSelection(PresetExpandableListAdapter.NOT_SELECTED, groupPos)
            viewModel.selected = listAdapter.getGroup(groupPos)

            if ((viewModel.selected as AmpPresetGroup).uid != null) {
                renameToolbarBtn.isEnabled = true
                deleteToolbarBtn.isEnabled = true
            } else {
                renameToolbarBtn.isEnabled = false
                deleteToolbarBtn.isEnabled = false
            }

            loadToolbarBtn.isEnabled = false

            true
        }

        presetsList.setOnItemLongClickListener { adapterView, view, pos, id ->
            if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(null, View.DragShadowBuilder(view), adapterView.getItemAtPosition(pos), 0)
                } else {
                    view.startDrag(null, View.DragShadowBuilder(view), adapterView.getItemAtPosition(pos), 0)
                }
            }
            false
        }

        presetsList.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val pos = presetsList.pointToPosition(event.x.toInt(), event.y.toInt())
                    val item = presetsList.getItemAtPosition(pos)
                    if (item != null && event.localState is AmpPreset) {
                        val group = when (item) {
                            is AmpPresetGroup -> item.uid
                            is AmpPreset -> item.group
                            else -> null
                        }
                        val preset = event.localState as AmpPreset
                        if (preset.group != group) {
                            doAsync {
                                viewModel.updatePreset(preset.copy(group = group))
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
                    when (val obj = viewModel.selected) {
                        is AmpPreset -> {
                            viewModel.selectPreset = obj
                            updateToolbarTitle()
                        }
                    }
                }
                R.id.create_preset_group -> {
                    Dialogs.showInputDialog(activity!!, "New group", "") { groupTitle ->
                        newGroup(groupTitle)
                    }
                }
                R.id.rename_preset_or_group -> {
                    val obj = viewModel.selected
                    if (obj != null) {
                        Dialogs.showInputDialog(activity!!, "Rename ", obj.toString())
                        { title ->
                            when (obj) {
                                is AmpPreset -> renamePreset(obj, title)
                                is AmpPresetGroup -> renameGroup(obj, title)
                            }
                        }
                    }
                }
                R.id.delete_preset_or_group -> {
                    val obj = viewModel.selected
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
        // todo
//        amp.selectPreset?.let {
//            toolbar.title = "${getString(R.string.presets_toolbar_title)} - ${it.title}"
//        }
    }

    private fun deleteItem(item: Any) {
        doAsync {
            when (item) {
                is AmpPreset -> viewModel.deletePreset(item)
                is AmpPresetGroup -> viewModel.deleteGroup(item)
            }
            onComplete {
                listAdapter.refresh()
            }
        }
    }

    private fun renameGroup(item: AmpPresetGroup, title: String) {
        doAsync {
            viewModel.updateGroup(item.copy(title = title))
            onComplete {
                listAdapter.refresh()
            }
        }
    }

    private fun renamePreset(item: AmpPreset, title: String) {
        doAsync {
            viewModel.updatePreset(item.copy(title = title))
            onComplete {
                listAdapter.refresh()
            }
        }
    }

    private fun newGroup(title: String) {
        doAsync {
            viewModel.insertGroup(AmpPresetGroup(title = title))
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

        startActivityForResult(intent, Constants.READ_REQUEST_CODE)
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == Constants.READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val context = activity!!
            resultData?.data?.let { uri ->
                val fileName = Utils.getFileName(context, uri)
                context.contentResolver.openInputStream(uri)?.let { inputStream ->
                    val ydfile = YdFile(inputStream)
                    val err = ydfile.errorReason()
                    if (err != null) {
                        alert(err) { okButton {} }.show()
                    } else {
                        ydfile.presetData()
                                ?.filter { !it.isInit() }
                                ?.let { presets ->
                                    Dialogs.showInputDialog(context,
                                            getString(R.string.enter_a_preset_name),
                                            fileName) { groupName ->
                                        doAsync {

                                            val groupId = viewModel
                                                    .insertGroup(AmpPresetGroup(title = groupName))
                                                    .toInt()

                                            presets.forEach {
                                                viewModel.insertPreset(
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
