package xyz.lazysoft.a3amp.view

import `in`.goodiebag.carouselpicker.CarouselPicker
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import it.beppi.knoblibrary.Knob
import xyz.lazysoft.a3amp.AmpApplication
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.components.AmpComponent
import xyz.lazysoft.a3amp.components.wrappers.AmpCarouselWrapper
import xyz.lazysoft.a3amp.components.wrappers.AmpKnobWrapper
import xyz.lazysoft.a3amp.persistence.AppDatabase
import javax.inject.Inject

abstract class AbstractThrFragment : Fragment() {

    @Inject
    lateinit var thr: Amp

    @Inject
    lateinit var repository: AppDatabase

    abstract val fragmentId: Int

    var fragmentView: View? = null

    override fun onAttach(context: Context) {
        (context.applicationContext as AmpApplication).component.inject(this)
        super.onAttach(context)
    }

    fun initCarousel(carousel: Int, content: Int, id: Int): AmpComponent<Int> {
        return initCarousel(carousel, content, id, null, null)
    }

    fun initCarousel(carousel: Int, content: Int, id: Int?, swId: Int?,
                     changeListener: ((mode: Int) -> Unit)?): AmpComponent<Int> {

        val carouselPicker = fragmentView!!.findViewById<CarouselPicker>(carousel)
        val ampCarouselWrapper = AmpCarouselWrapper(carouselPicker, thr, id)
        ampCarouselWrapper.setContent(content, fragmentView!!.context)
        if (changeListener != null) {
            ampCarouselWrapper.swId = swId
            ampCarouselWrapper.setOnStateChanged(changeListener)
        }

        thr.dump.observe(this, ampCarouselWrapper.observe)

        return ampCarouselWrapper
    }

    fun initKnob(knob: Knob, text: TextView, id: Int): AmpComponent<Int> {
        return initKnob(knob, text, id, null)
    }

    fun initKnob(knob: Knob, text: TextView, id: Int, range: Pair<Int, Int>?): AmpComponent<Int> {
        val ampKnobWrapper = AmpKnobWrapper(knob, id, thr, range)

        ampKnobWrapper.setOnStateChanged {
            text.text = it.toString()
        }
        thr.dump.observe(this, ampKnobWrapper.observe)

        return ampKnobWrapper
    }

    fun blockActivator(vararg ids: Int): (value: Int) -> Unit {
        return { value ->
            ids.map { fragmentView!!.findViewById<View>(it) }
                    .withIndex()
                    .forEach { (index, view) ->
                        view.visibility = if (index == value) View.VISIBLE else View.GONE
                    }
        }
    }

    fun <T : View?> find(id: Int): T {
        return fragmentView!!.findViewById<T>(id)
    }

    abstract fun initFragment()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(fragmentId, container, false)
        initFragment()
        return fragmentView
    }

    companion object {
        fun listFragments(): List<AbstractThrFragment> {
            return listOf(
                    AmpFragment(),
                    CompressorFragment(),
                    EffectsFragment(),
                    DelayFragment(),
                    ReverbFragment(),
                    GateFragment())
        }
    }
}