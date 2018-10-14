package xyz.lazysoft.a3amp.components.wrappers

import android.annotation.SuppressLint
import android.app.Activity
import android.util.ArraySet
import android.widget.Switch
import xyz.lazysoft.a3amp.components.AmpComponent

@SuppressLint("Registered")
class AmpSwitchWrapper(private val sw: Switch) : Activity(), AmpComponent<Boolean> {

    private var onStateChangeFunctions: ArraySet<(state: Boolean) -> Unit> = ArraySet()

    init {
        sw.setOnCheckedChangeListener { _, isChecked ->
            onStateChangeFunctions.forEach {
                it.invoke(isChecked)
            }
        }
    }

    override fun setOnStateChanged(function: (isChecked: Boolean) -> Unit) {
        onStateChangeFunctions.add(function)
    }

    override var state: Boolean
        get() = sw.isChecked
        set(value) {
            runOnUiThread { sw.isChecked = value }
        }

}