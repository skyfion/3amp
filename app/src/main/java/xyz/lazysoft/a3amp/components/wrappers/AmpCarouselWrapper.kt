package xyz.lazysoft.a3amp.components.wrappers

import `in`.goodiebag.carouselpicker.CarouselPicker
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.ArraySet
import androidx.viewpager.widget.ViewPager
import xyz.lazysoft.a3amp.amp.Constants
import xyz.lazysoft.a3amp.components.AmpComponent

@SuppressLint("Registered")
class AmpCarouselWrapper(private val carousel: CarouselPicker) : Activity(),
        AmpComponent<Int>, ViewPager.OnPageChangeListener {

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    init {
        carousel.addOnPageChangeListener(this)
    }

    private var onSelectFunction: ArraySet<(pos: Int) -> Unit> = ArraySet()

    override fun setOnStateChanged(function: (pos: Int) -> Unit): AmpComponent<Int> {
        onSelectFunction.add(function)
        return this
    }

    override var state: Int
        get() = carousel.currentItem
        set(value) {
            runOnUiThread { carousel.currentItem = value }
        }

    override fun onPageSelected(position: Int) {
        onSelectFunction.forEach { it.invoke(state) }
    }

    fun setContent(content: Int, context: Context) {
       val textItems = context.resources.getStringArray(content).map {
           CarouselPicker.TextItem(it, Constants.CAROUSEL_TEXT_SIZE) }
        carousel.adapter = CarouselPicker.CarouselViewAdapter(context, textItems, 0)
    }
}