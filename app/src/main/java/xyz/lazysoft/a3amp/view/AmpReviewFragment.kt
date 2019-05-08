package xyz.lazysoft.a3amp.view

import android.os.Bundle
import android.provider.SyncStateContract
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.support.v4.toast
import xyz.lazysoft.a3amp.R
import xyz.lazysoft.a3amp.amp.Constants
import xyz.lazysoft.a3amp.components.preview.AmpPreviewAdapter
import xyz.lazysoft.a3amp.components.preview.AmpUnit
import xyz.lazysoft.a3amp.view.model.AmpPreviewViewModel

class AmpReviewFragment : AbstractThrFragment() {

    private var review: RecyclerView? = null

    private val unitList: Map<Int, AmpUnit> by lazy {
        mapOf(
                Constants.AMP to AmpUnit(getString(R.string.amp), false)
                { showFragment(AmpFragment()) },
                Constants.COMPRESSOR_SW to AmpUnit(getString(R.string.compressor), true)
                { showFragment(CompressorFragment()) },
                Constants.EFFECTS_SW to AmpUnit(getString(R.string.effect), true)
                { showFragment(EffectsFragment()) },
                Constants.DELAY_SW to AmpUnit(getString(R.string.delay), true)
                { showFragment(DelayFragment()) },
                Constants.DELAY_SW to AmpUnit(getString(R.string.reverb), true)
                { showFragment(ReverbFragment()) },
                Constants.GATE_SW to AmpUnit(getString(R.string.gate), true)
                { showFragment(GateFragment()) }
        )
    }

    private var viewModel: AmpPreviewViewModel? = null

    override val fragmentId: Int
        get() = R.layout.amp_preview

    private fun showFragment(fragment: AbstractThrFragment) {
        activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, fragment)
                ?.commit()
    }

    override fun initFragment() {
        // fragmentView
        review = find<RecyclerView>(R.id.amp_preview_list)
        val manager = LinearLayoutManager(this.context)

        review?.layoutManager = manager
        review?.adapter = AmpPreviewAdapter(unitList.values.toList())

        viewModel?.gain()?.observe(this, Observer {
            toast("gain $it")
        })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AmpPreviewViewModel::class.java)

    }
}