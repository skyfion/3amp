package xyz.lazysoft.a3amp.components.presets

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import xyz.lazysoft.a3amp.R

class PresetExpandableListAdapter(val context: Context, private val detail: HashMap<String, List<String>>) : BaseExpandableListAdapter() {

    private var titleList: List<String> = detail.keys.toList()

    override fun getGroup(listPosition: Int): Any {
        return titleList[listPosition]
    }

    override fun isChildSelectable(listPosition: Int, expandedListPOsition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(listPosition: Int, isExpanded: Boolean,
                              view: View?, parent: ViewGroup?): View {
        val listTitle = getGroup(listPosition) as String
        val listView = if (view == null) {
            val layout = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            layout.inflate(R.layout.preset_group, null)
        } else
            view
        val listTitleTextVie = listView.findViewById<TextView>(R.id.listTitle)
        listTitleTextVie.setTypeface(null, Typeface.BOLD)
        listTitleTextVie.text = listTitle
        return listView
    }

    override fun getChildrenCount(p0: Int): Int {
        return detail[titleList[p0]]?.size ?: 0
    }

    override fun getChild(p0: Int, p1: Int): Any {
        return detail[titleList[p0]]!![p1]
    }

    override fun getGroupId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val listText = getChild(p0, p1) as String
        val listView = if (p3 == null) {
            val layoutInflater = context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            layoutInflater.inflate(R.layout.list_item, null)
        } else
            p3
        val textView = listView.findViewById<TextView>(R.id.expandedListItem)
        textView.text = listText
        return listView
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        return p1.toLong()
    }

    override fun getGroupCount(): Int {
        return titleList.size
    }

}