package xyz.lazysoft.a3amp.components.preview

data class AmpUnit(
        val title: String,
        val sw: Boolean,
        val onClick: () -> Unit)