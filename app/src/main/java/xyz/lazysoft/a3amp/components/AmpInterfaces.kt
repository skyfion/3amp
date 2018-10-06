package xyz.lazysoft.a3amp.components


interface AmpComponent<T> {
    fun setOnStateChanged(function: (value: T) -> Unit)
    var state: T
}