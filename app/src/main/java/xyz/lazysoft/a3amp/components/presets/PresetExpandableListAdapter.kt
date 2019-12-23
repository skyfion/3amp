package xyz.lazysoft.a3amp.components.presets

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread
import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AmpPresetGroup
import java.util.*

class PresetExpandableListAdapter(val context: Context,
                                  private val groups: List<AmpPresetGroup>,
                                  private val presets: List<AmpPreset>)
    : BaseExpandableListAdapter() {
    private var selectedChildPos = NOT_SELECTED
    private var selectedGroupPos = NOT_SELECTED
    private var detail = HashMap<AmpPresetGroup, List<AmpPreset>>()

    init {
        refresh()
    }

    fun refresh() {
        doAsync {
            detail.clear()
            val groups = groups.toMutableList()
            groups.add(AmpPresetGroup(uid = null, title = "Custom")) // default group

            val presets = presets.groupBy { it.group }
            groups.forEach { g ->
                detail[g] = if (presets.containsKey(g.uid)) {
                    presets.getValue(g.uid)
                } else {
                    emptyList()
                }
            }
            onComplete {
                uiThread {
                    it.notifyDataSetChanged()
                }
            }
        }
    }

    fun setSelection(childPos: Int, groupPos: Int) {
        selectedChildPos = childPos
        selectedGroupPos = groupPos
        notifyDataSetChanged()
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
        val listView =
                if (view == null) {
                    val layout = context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    layout.inflate(R.layout.preset_group, null)
                } else
                    view
        listView.isClickable = false
        val listTitleTextView = listView.findViewById<TextView>(R.id.listTitle)
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = group.title
        selectView(listPosition, NOT_SELECTED, listView)
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
        val listView =
                if (view == null) {
                    val layoutInflater = context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    layoutInflater.inflate(R.layout.list_item, null)
                } else
                    view
        listView.isClickable = false
        val textView = listView.findViewById<TextView>(R.id.expandedListItem)
        textView.text = preset.title
        selectView(groupPos, childPos, listView)
        return listView
    }


    private fun setTextColor(view: View, color: Int) {
        val liner = view as? LinearLayout
        liner?.let {
            val text = it.getChildAt(0) as? TextView
            text?.setTextColor(color)
        }
    }

    private fun selectView(groupPos: Int, childPos: Int, view: View) {
        if (childPos == selectedChildPos && groupPos == selectedGroupPos) {
            // your color for selected item
            view.setBackgroundColor(Color.parseColor("#ffab00"))
            setTextColor(view, Color.BLACK)
        } else {
            // your color for non-selected item
            view.setBackgroundColor(Color.TRANSPARENT)
            setTextColor(view, Color.WHITE)
        }
    }

    override fun getChildId(groupPos: Int, childPos: Int): Long {
        return childPos.toLong()
    }

    override fun getGroupCount(): Int {
        return detail.keys.size
    }

    companion object {
        const val NOT_SELECTED = -1
    }


}