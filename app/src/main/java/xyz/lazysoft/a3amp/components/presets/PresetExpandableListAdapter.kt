package xyz.lazysoft.a3amp.components.presets

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread
import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AmpPresetGroup
import xyz.lazysoft.a3amp.persistence.PresetDao

class PresetExpandableListAdapter(val context: Context, private val dao: PresetDao) : BaseExpandableListAdapter() {

    private var detail = HashMap<AmpPresetGroup, List<AmpPreset>>()

    init {
        refresh()
    }

    fun refresh() {
        doAsync {
            detail.clear()
            val groups = dao.getAllGroups()
            val presets = dao.getAll().groupBy { it.group }
            presets.keys.forEach { key ->
                groups.find { g -> g.uid == key }?.let {g ->
                    detail[g] = presets.getValue(key)
                }
            }
            onComplete {
                uiThread {
                    it.notifyDataSetChanged()
                }
            }
        }
    }

    fun rename() {

    }



    fun addNewGroup(title: String) {
        doAsync {
            dao.insertGroup(AmpPresetGroup(title = title))
            onComplete {
                refresh()
            }
        }
    }

    override fun getGroup(listPosition: Int): Any {
        return detail.keys.toList()[listPosition]
    }

    override fun isChildSelectable(listPosition: Int, expandedListPOsition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(listPosition: Int, isExpanded: Boolean,
                              view: View?, parent: ViewGroup?): View {
        val group = getGroup(listPosition) as AmpPresetGroup
        val listView = if (view == null) {
            val layout = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            layout.inflate(R.layout.preset_group, null)
        } else
            view
        val listTitleTextView = listView.findViewById<TextView>(R.id.listTitle)
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = group.title
        return listView
    }

    override fun getChildrenCount(p0: Int): Int {
        return detail[getGroup(p0)]?.size ?: 0
    }

    override fun getChild(groupPos: Int, childPos: Int): Any {
        return detail[getGroup(groupPos)]!![childPos]
    }

    override fun getGroupId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getChildView(groupPos: Int, childPos: Int, p2: Boolean,
                              view: View?, viewGroup: ViewGroup?): View {
        val preset = getChild(groupPos, childPos) as AmpPreset
        val listView = if (view == null) {
            val layoutInflater = context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            layoutInflater.inflate(R.layout.list_item, null)
        } else
            view
        val textView = listView.findViewById<TextView>(R.id.expandedListItem)
        textView.text = preset.title
        return listView
    }

    override fun getChildId(groupPos: Int, childPos: Int): Long {
        return childPos.toLong()
    }

    override fun getGroupCount(): Int {
        return detail.keys.size
    }

}