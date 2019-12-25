package xyz.lazysoft.a3amp.components.wrappers


interface AmpComponent<T> {
    fun setOnStateChanged(function: (value: T) -> Unit): AmpComponent<T>
    var state: T
}