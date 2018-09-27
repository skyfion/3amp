package xyz.lazysoft.a3amp.components

import android.widget.Switch
import it.beppi.knoblibrary.Knob

class AmpKnobWrapper(val knob: Knob): AmpKnob {
    override fun setOnStateChanged(function: (value: Int) -> Unit) {
        knob.setOnStateChanged(function)
    }

    override var state: Int
        get() = knob.state
        set(value) { knob.state = value}

}

class AmpSwitchWrapper(val sw: Switch): AmpSwitch {
    override fun setOnCheckedChangeListener(function: (isChecked: Boolean) -> Unit) {
        sw.setOnCheckedChangeListener{_,isChecked ->
            function.invoke(isChecked)
        }
    }

    override var isChecked: Boolean
        get() = sw.isChecked
        set(value) {sw.isChecked = value}

}