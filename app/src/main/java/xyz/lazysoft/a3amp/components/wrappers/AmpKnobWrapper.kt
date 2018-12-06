package xyz.lazysoft.a3amp.components.wrappers

import android.annotation.SuppressLint
import android.app.Activity
import android.util.ArraySet
import it.beppi.knoblibrary.Knob
import xyz.lazysoft.a3amp.components.AmpComponent

@SuppressLint("Registered")
class AmpKnobWrapper(val knob: Knob, toRange: Pair<Int, Int>?) : Activity(), AmpComponent<Int> {

    private var onSelectFunction: ArrayList<(pos: Int) -> Unit> = ArrayList()
    private val interpolator: LinearInterpolator? = toRange?.let { LinearInterpolator(it) }

    init {
        knob.setOnStateChanged { value ->
            onSelectFunction.forEach { f ->
                f.invoke(interpolator?.valueInterpolated(value) ?: value)
            }
        }
    }

    override fun setOnStateChanged(function: (value: Int) -> Unit): AmpComponent<Int> {
        onSelectFunction.add(function)
        return this
    }

    override var state: Int
        get() = interpolator?.valueInterpolated(knob.state) ?: knob.state
        set(value) {
            runOnUiThread { knob.state = interpolator?.valueToRange(value) ?: value }
        }

    class LinearInterpolator(val to: Pair<Int, Int>) {
        private val y1 = to.first
        private val y2 = to.second

        fun valueInterpolated(x: Int): Int {
            return y1 + (y2 - y1) * x / 100
        }

        fun valueToRange(d: Int): Int {
            return 100 * (d - y1) / (y2 - y1)
        }
    }
}