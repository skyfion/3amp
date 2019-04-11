package xyz.lazysoft.a3amp.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.components.preview.AmpPreviewAdapter
import xyz.lazysoft.a3amp.components.preview.AmpUnit

class AmpReviewFragment : AbstractThrFragment() {
    override val fragmentId: Int
        get() = R.layout.amp_preview

    override fun initFragment() {
        // fragmentView
        val review = find<RecyclerView>(R.id.amp_preview_list)
        val manager = LinearLayoutManager(this.context)
        val unitList = listOf(
                AmpUnit(AmpFragment(), "Amp", false),
                AmpUnit(CompressorFragment(), "Compressor", true),
                AmpUnit(EffectsFragment(), "Effects", true),
                AmpUnit(DelayFragment(), "Delay", true),
                AmpUnit(ReverbFragment(), "Reverb", true),
                AmpUnit(GateFragment(), "Gate", true)
        )
        review.layoutManager = manager
        review.adapter = AmpPreviewAdapter(unitList)
    }

}