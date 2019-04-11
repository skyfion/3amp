package xyz.lazysoft.a3amp.components.wrappers

import `in`.goodiebag.carouselpicker.CarouselPicker
import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.amp.Constants
import xyz.lazysoft.a3amp.amp.Constants.Companion.SEND_CMD
import xyz.lazysoft.a3amp.amp.PresetDump
import xyz.lazysoft.a3amp.amp.Utils
import xyz.lazysoft.a3amp.components.AmpComponent

@SuppressLint("Registered")
class AmpCarouselWrapper(private val carousel: CarouselPicker, private val thr: Amp, val id: Int?) :
        AmpComponent<Int>, ViewPager.OnPageChangeListener {

    var swId: Int? = null

    override val observe: Observer<PresetDump> = Observer { dump ->
        dump.getValueById(id!!)?.let { v ->
            val param = Utils.paramToInt(v)
            if (swId != null) {
                dump.getValueById(swId!!)?.let { sw ->
                    state = if (sw[1] == Constants.OFF.toByte()) {
                        0
                    } else {
                        param.inc()
                    }
                }
            } else {
                state = param
            }
        }
    }

    private val startCmdID: ByteArray by lazy {
        SEND_CMD + id!!.toByte() + 0x00
    }

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
                    thr.sendCommand(startCmdID + Utils.byteArrayOf(v - 1, Constants.END))
                }
            } else {
                thr.sendCommand(startCmdID + v.toByte() + Constants.END.toByte())
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
            doAsync {
                uiThread {
                    carousel.currentItem = value
                }
            }
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