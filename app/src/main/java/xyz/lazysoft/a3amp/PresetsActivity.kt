package xyz.lazysoft.a3amp

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView.INVALID_POSITION
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AppDatabase
import javax.inject.Inject


class PresetsActivity : AppCompatActivity() {

    var presets: List<AmpPreset>? = null
    private lateinit var presetsList: ListView

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

        presetsList = findViewById(R.id.presets_list_view)
        initListPresets()

        presetsList.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            presets?.let {
                amp.selectPreset = it[position]
            }
        }

    }

    private fun initListPresets() {
        doAsync {
            presets = repository.presetDao().getAll()
            uiThread { activity ->
                val presetsAdapter = ArrayAdapter<AmpPreset>(activity,
                        android.R.layout.simple_list_item_1,
                        presets)
                amp.selectPreset?.let {
                    if (presets != null)
                        presetsList.setItemChecked(presets!!.indexOf(it), true)
                }
                presetsList.adapter = presetsAdapter
                presetsAdapter.notifyDataSetChanged()
            }
        }
    }

    fun deletePreset(position: Int) {
        if (position != INVALID_POSITION) {
            presets?.get(position)?.let {p ->
                alert("Delete preset ${p.title}", "Delete") {
                    yesButton {
                        doAsync {
                            repository.presetDao().delete(p)
                        }
                        val adapter = presetsList.adapter as ArrayAdapter<AmpPreset>
                        presetsList.clearChoices()
                        presetsList.setItemChecked(-1, true)
                        adapter.remove(p)
                        adapter.notifyDataSetChanged()
                        amp.selectPreset = null
                    }
                    noButton {}
                }.show()

            }
        }
    }

    fun renamePreset(position: Int) {
        // todo this is copy paste from MainActivity
        if (position != INVALID_POSITION) {
            presets?.get(position)?.let { p ->
                lateinit var dialog: DialogInterface
                lateinit var presetName: EditText
                dialog = alert {
                    title = "Rename"
                    isCancelable = true
                    customView {
                        verticalLayout {
                            linearLayout() {
                                presetName = editText(p.title) {
                                    hint = "enter name"
                                }.lparams(width = matchParent)
                            }.lparams(width = matchParent)
                            linearLayout {
                                button("Cancel") {
                                    onClick {
                                        dialog.dismiss()
                                    }
                                }
                                button("OK") {
                                    onClick {
                                        val presetTitle = presetName.text.toString()
                                        if (TextUtils.isEmpty(presetTitle)) {
                                            presetName.error = "Enter name!"
                                        } else {
                                            if (p.title != presetTitle) {
                                                p.title = presetTitle
                                                doAsync {
                                                    repository.presetDao().update(p)
                                                }
                                                initListPresets()
                                                (presetsList.adapter as ArrayAdapter<*>)
                                                        .notifyDataSetChanged()
                                            }
                                            dialog.dismiss()
                                        }
                                    }
                                }
                            }.lparams(width = matchParent)
                        }
                    }
                }.show()
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.presets_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_preset_menu -> {
                deletePreset(presetsList.checkedItemPosition)
                true
            }
            R.id.rename_preset_menu -> {
                renamePreset(presetsList.checkedItemPosition)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
