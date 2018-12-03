package xyz.lazysoft.a3amp.components

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.preset_item.view.*
import org.jetbrains.anko.*
import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AmpPresetDao

class PresetAdapter(val context: Context, private val dao: AmpPresetDao, private val thr: Amp) : RecyclerView.Adapter<PresetAdapter.PresetViewHolder>() {

    var presets: ArrayList<AmpPreset> = ArrayList()
    var loadedPosition: Int = -1

    init {
       refresh()
    }

    fun refresh() {
        doAsync {
            val result = dao.getAll() as ArrayList<AmpPreset>
            uiThread {
                presets.clear()
                presets.addAll(result)
                it.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.preset_item, parent, false)

        return PresetViewHolder(view)
    }

    override fun getItemCount(): Int {
        return presets.count()
    }

    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) {
        holder.view.name.text = presets[position].title
        if (position == loadedPosition)
            holder.view.btnLoadPreset.linkTextColor = R.color.white
        holder.view.btnDelete.setOnClickListener { showDeleteDialog(holder, presets[position], position) }
        holder.view.btnEdit.setOnClickListener { showUpdateDialog(holder, presets[position]) }
        holder.view.btnLoadPreset.setOnClickListener { loadPreset(presets[position], position) }
    }


    private fun deletePreset(preset: AmpPreset) {
        doAsync {
            dao.delete(preset)
            onComplete {
                uiThread {
                    it.refresh()
                    thr.selectPreset?.let { selectPreset ->
                        if (selectPreset.uid != null && preset.uid != null &&
                                preset.uid == selectPreset.uid) {
                            thr.selectPreset = null
                        }
                    }
                }
            }
        }
    }

    private  fun updatePreset(preset: AmpPreset) {
        doAsync {
            dao.update(preset)
            onComplete {
                uiThread {
                    it.refresh()
                }
            }
        }
    }

    fun loadPreset(preset: AmpPreset, position: Int) {
        loadedPosition = position
        thr.selectPreset = preset
        notifyDataSetChanged()
    }

    fun showUpdateDialog(holder: PresetViewHolder, preset: AmpPreset) {
        val dialogBuilder = AlertDialog.Builder(holder.view.context)

        val input = EditText(holder.view.context)
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = lp
        input.setText(preset.title)

        dialogBuilder.setView(input)

        dialogBuilder.setTitle("Update name preset")
        dialogBuilder.setPositiveButton("Update") { _, _ ->
            updatePreset(preset.copy(title = input.text.toString()))
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        val b = dialogBuilder.create()
        b.show()
    }

    private fun showDeleteDialog(holder: PresetViewHolder, preset: AmpPreset, position: Int) {
        val dialogBuilder = AlertDialog.Builder(holder.view.context)
        dialogBuilder.setTitle("Delete")
        dialogBuilder.setMessage("Confirm delete?")
        dialogBuilder.setPositiveButton("Delete") { _, _ ->
            if (position == loadedPosition) loadedPosition = -1
            deletePreset(preset)
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        val b = dialogBuilder.create()
        b.show()
    }


    class PresetViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}