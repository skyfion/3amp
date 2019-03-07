package xyz.lazysoft.a3amp.components.presets

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import xyz.lazysoft.a3amp.R

class PresetExpandableListAdapter(val context: Context, val title: List<String>, val detail: HashMap<String, List<String>>): BaseExpandableListAdapter() {

    override fun getGroup(listPosition: Int): Any {
           return title[listPosition]
    }

    override fun isChildSelectable(listPosition: Int, expandedListPOsition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(listPosition: Int, isExpanded: Boolean,
                              view: View?, parent: ViewGroup?): View {
        val listTitle = getGroup(listPosition)

        if (view == null) {
            val layout = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layout.inflate(R.layout.preset_group, null)
        }
    }

    override fun getChildrenCount(p0: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChild(p0: Int, p1: Int): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGroupId(p0: Int): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGroupCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}