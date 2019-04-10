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
class AmpCarouselWrapper(private val carousel: CarouselPicker,
                         private val thr: Amp, val id: Int?) : Activity(),
        AmpComponent<Int>, ViewPager.OnPageChangeListener {

    var swId: Int? = null

    override val observe: Observer<PresetDump> = Observer { dump ->
        dump.getValueById(id)?.let { v ->
            val param = Utils.paramToInt(v)
            if (swId != null) {
                dump.getValueById(swId!!)?.let { sw ->
                    if (sw[1] == Constants.OFF.toByte()) {
                        state = 0
                    } else {
                        state = param.inc()
                    }
                }
            } else {
                state = param
            }
        }
    }

    private val startCmdID = SEND_CMD + id.toByte() + 0x00


    private var onSelectFunction: MutableSet<(pos: Int) -> Unit> = mutableSetOf()

    init {
        carousel.addOnPageChangeListener(this)
        onSelectFunction.add { v ->
            if (swId != null) {
                if (v == 0) {
                    thr.sendCommand(SEND_CMD +
                            Utils.byteArrayOf(swId!!, 0x00, Constants.OFF, Constants.END))
                } else {
                    thr.sendCommand(SEND_CMD +
                            Utils.byteArrayOf(swId!!, 0x00, Constants.ON, Constants.END))
                    thr.sendCommand(startCmdID + Utils.byteArrayOf(v - 1, END))
                }
            } else {
                thr.sendCommand(startCmdID + v.toByte() + END.toByte())
            }
        }
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