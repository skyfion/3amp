package xyz.lazysoft.a3amp.components.wrappers

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import it.beppi.knoblibrary.Knob
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.amp.Constants
import xyz.lazysoft.a3amp.amp.PresetDump
import xyz.lazysoft.a3amp.amp.Utils
import xyz.lazysoft.a3amp.components.AmpComponent

@SuppressLint("Registered")
class AmpKnobWrapper(val knob: Knob, val id: Int, val thr: Amp, toRange: Pair<Int, Int>?) : AmpComponent<Int> {

    override val observe: Observer<PresetDump> = Observer { dump ->
        dump.getValueById(id)?.let { v ->
            val newV = Utils.paramToInt(v)
            if (state != newV)
                state = newV
        }
    }

    private val startCmdID = Constants.SEND_CMD + id.toByte()

    private var onSelectFunction: ArrayList<(pos: Int) -> Unit> = ArrayList()
    private val interpolator: LinearInterpolator? = toRange?.let { LinearInterpolator(it) }

    init {
        interpolator?.let { fn ->
            doAsync {
                uiThread {
                    knob.state = fn.valueToRange(toRange!!.first)
                }
            }
        }

        knob.setOnStateChanged { value ->
            onSelectFunction.forEach { f ->
                f.invoke(interpolator?.valueInterpolated(value) ?: value)
            }
        }
        setOnStateChanged { v ->
            thr.midiManager
                    .sendSysExCmd(startCmdID + Utils.intToParam(v) + Constants.END.toByte())
        }
    }

    override fun setOnStateChanged(function: (value: Int) -> Unit): AmpComponent<Int> {
        onSelectFunction.add(function)
        return this
    }

    private fun checkRange(value: Int): Boolean {
        val range: IntRange = interpolator?.let {
            IntRange(it.y1, it.y2)
        } ?: run {
            IntRange(0, 100)
        }
        return (value in range)
    }

    override var state: Int
        get() = interpolator?.valueInterpolated(knob.state) ?: knob.state
        set(value) {
            if (checkRange(value)) {
                doAsync {
                    uiThread {
                        knob.state = interpolator?.valueToRange(value) ?: value
                    }
                }
            }
        }

    class LinearInterpolator(val to: Pair<Int, Int>) {
        val y1 = to.first
        val y2 = to.second

        fun valueInterpolated(x: Int): Int {
            return y1 + (y2 - y1) * x / 100
        }

        fun valueToRange(d: Int): Int {
            return 100 * (d - y1) / (y2 - y1)
        }
    }
}