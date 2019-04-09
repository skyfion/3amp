package xyz.lazysoft.a3amp.components.wrappers

import `in`.goodiebag.carouselpicker.CarouselPicker
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.amp.Constants
import xyz.lazysoft.a3amp.amp.Constants.Companion.END
import xyz.lazysoft.a3amp.amp.Constants.Companion.SEND_CMD
import xyz.lazysoft.a3amp.amp.PresetDump
import xyz.lazysoft.a3amp.amp.Utils
import xyz.lazysoft.a3amp.components.AmpComponent

@SuppressLint("Registered")
class AmpCarouselWrapper(private val carousel: CarouselPicker, private val thr: Amp, val id: Int) : Activity(),
        AmpComponent<Int>, ViewPager.OnPageChangeListener {

    override val observe: Observer<PresetDump> = Observer { dump ->
        dump.getValueById(id)?.let { v ->
            state = Utils.paramToInt(v)
        }
    }

    private val startCmdID = SEND_CMD + id.toByte() + 0x00

    private var onSelectFunction: MutableSet<(pos: Int) -> Unit> = mutableSetOf()

    init {
        carousel.addOnPageChangeListener(this)
        onSelectFunction.add { v -> thr.sendCommand(startCmdID + v.toByte() + END.toByte()) }
    }

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
            CarouselPicker.TextItem(it, Constants.CAROUSEL_TEXT_SIZE)
        }
        carousel.adapter = CarouselPicker.CarouselViewAdapter(context, textItems, 0)
    }


    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
}