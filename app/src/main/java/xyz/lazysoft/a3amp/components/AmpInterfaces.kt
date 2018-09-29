package xyz.lazysoft.a3amp.components


interface AmpKnob {
    fun setOnStateChanged(function: (value: Int) -> Unit)
    var state: Int

}

interface AmpSwitch {
    fun setOnCheckedChangeListener(function: (isChecked: Boolean) -> Unit)
    var isChecked: Boolean
}


interface AmpSpinner {
    fun setOnSelectItem(function: (pos: Int) -> Unit)
    var pos: Int
}