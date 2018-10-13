package xyz.lazysoft.a3amp.components.wrappers

import android.annotation.SuppressLint
import android.app.Activity
import android.util.ArraySet
import it.beppi.knoblibrary.Knob
import xyz.lazysoft.a3amp.components.AmpComponent

@SuppressLint("Registered")
class AmpKnobWrapper(val knob: Knob, toRange: Pair<Int, Int>?) : Activity(), AmpComponent<Int> {

    private var onSelectFunction: ArraySet<(pos: Int) -> Unit> = ArraySet()
    private var interpolator: LinearInterpolator? = null

    init {
        toRange?.let { interpolator = LinearInterpolator(it) }
        knob.setOnStateChanged { value ->
            onSelectFunction.forEach { f ->
                f.invoke(interpolator?.valueInterpolated(value) ?: value)
            }
        }
    }

    override fun setOnStateChanged(function: (value: Int) -> Unit) {
        onSelectFunction.add(function)
    }

    override var state: Int
        get() = interpolator?.valueInterpolated(knob.state) ?: knob.state
        set(value) {
            runOnUiThread { knob.state = interpolator?.valueToRange(value) ?: value }
        }

    class LinearInterpolator(val to: Pair<Int, Int>) {
        val x1 = 0
        val x2 = 100
        val y1 = to.first
        val y2 = to.second

        fun valueInterpolated(x: Int): Int {
            return y1 + (y2 - y1) * (x - x1) / (x2 - x1)
        }

        fun valueToRange(d: Int): Int {
            return x1 + (x2 - x1) * (d - y1) / (y2 - y1)
        }
    }
}