package xyz.lazysoft.a3amp.components


interface AmpComponent<T> {
    fun setOnStateChanged(function: (value: T) -> Unit): AmpComponent<T>
    var state: T
}