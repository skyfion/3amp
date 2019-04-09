package xyz.lazysoft.a3amp.components

import androidx.lifecycle.Observer
import xyz.lazysoft.a3amp.amp.PresetDump


interface AmpComponent<T> {
    fun setOnStateChanged(function: (value: T) -> Unit): AmpComponent<T>
    var state: T
    val observe: Observer<PresetDump>

}