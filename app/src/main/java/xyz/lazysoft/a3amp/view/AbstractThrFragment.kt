package xyz.lazysoft.a3amp.view

import `in`.goodiebag.carouselpicker.CarouselPicker
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import xyz.lazysoft.a3amp.AmpApplication
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.components.AmpComponent
import xyz.lazysoft.a3amp.components.wrappers.AmpCarouselWrapper
import xyz.lazysoft.a3amp.components.wrappers.AmpKnobWrapper
import javax.inject.Inject

abstract class AbstractThrFragment: Fragment() {

    @Inject
    lateinit var thr: Amp

    abstract val fragmentId: Int

    var fragmentView: View? = null

    override fun onAttach(context: Context) {
        (context.applicationContext as AmpApplication).component.inject(this)
        super.onAttach(context)
    }

    fun initCarousel(carousel: Int, content: Int): AmpComponent<Int> {
        return initCarousel(carousel, content, null)
    }

    fun initCarousel(carousel: Int, content: Int,
                             changeListener: ((mode: Int) -> Unit)?): AmpComponent<Int> {

        val carouselPicker = fragmentView!!.findViewById<CarouselPicker>(carousel)
        val ampCarouselWrapper = AmpCarouselWrapper(carouselPicker)
        ampCarouselWrapper.setContent(content, fragmentView!!.context)
        if (changeListener != null)
            ampCarouselWrapper.setOnStateChanged(changeListener)
        return ampCarouselWrapper
    }

    fun initKnob(knob: Int, text: Int): AmpComponent<Int> {
        return initKnob(knob, text, null)
    }

    fun initKnob(knob: Int, text: Int, range: Pair<Int, Int>?): AmpComponent<Int> {
        val ampKnobWrapper = AmpKnobWrapper(fragmentView!!.findViewById(knob), range)
        val knobText = fragmentView!!.findViewById<TextView>(text)

        ampKnobWrapper.setOnStateChanged {
            knobText.text = it.toString()
        }
        return ampKnobWrapper
    }

    fun blockActivator(vararg ids: Int): (value: Int) -> Unit {
        return { value ->
            ids.map { fragmentView!!.findViewById<View>(it) }
                    .withIndex()
                    .forEach { (index, view) -> view.visibility = if (index == value) View.VISIBLE else View.GONE }
        }
    }

    abstract fun initFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(fragmentId, container, false)
        initFragment()
        return fragmentView
    }
}