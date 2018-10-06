package xyz.lazysoft.a3amp.components

import `in`.goodiebag.carouselpicker.CarouselPicker
import android.annotation.SuppressLint
import android.app.Activity
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Switch
import it.beppi.knoblibrary.Knob

@SuppressLint("Registered")
class AmpKnobWrapper(val knob: Knob) : Activity(), AmpComponent<Int> {

    override fun setOnStateChanged(function: (value: Int) -> Unit) {
        knob.setOnStateChanged(function)
    }

    override var state: Int
        get() = knob.state
        set(value) { runOnUiThread { knob.state = value } }
}

@SuppressLint("Registered")
class AmpSpinnerWrapper(val s: Spinner) : Activity(), AdapterView.OnItemSelectedListener, AmpComponent<Int> {

    private var onSelectFunction: ((pos: Int) -> Unit)? = null

    override fun setOnStateChanged(function: (pos: Int) -> Unit) {
        onSelectFunction = function
    }

    override var state: Int
        get() = s.selectedItemPosition
        set(value) {
            runOnUiThread { s.setSelection(value) }
        }

    init {
        s.onItemSelectedListener = this
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        onSelectFunction?.invoke(p2)
    }
}

@SuppressLint("Registered")
class AmpCarouselWrapper(private val carousel: CarouselPicker) : Activity(),
        AmpComponent<Int>, ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    init {
        carousel.addOnPageChangeListener(this)
    }

    private var onSelectFunction: ArrayList<(pos: Int) -> Unit> = ArrayList()

    override fun setOnStateChanged(function: (pos: Int) -> Unit) {
        onSelectFunction.add(function)
    }

    override var state: Int
        get() = carousel.currentItem
        set(value) {
            runOnUiThread { carousel.currentItem = value }
        }

    override fun onPageSelected(position: Int) {
        onSelectFunction.forEach{ it.invoke(position)}
    }
}

@SuppressLint("Registered")
class AmpSwitchWrapper(private val sw: Switch) : Activity(), AmpComponent<Boolean> {

    private var onStateChangeFunctions: ArrayList<(state: Boolean) -> Unit> = ArrayList()

    init {
        sw.setOnCheckedChangeListener { _, isChecked ->
            onStateChangeFunctions.forEach{
                it.invoke(isChecked)
            }
        }
    }
    override fun setOnStateChanged(function: (isChecked: Boolean) -> Unit) {
        onStateChangeFunctions.add(function)
    }

    override var state: Boolean
        get() = sw.isChecked
        set(value) { runOnUiThread { sw.isChecked = value } }

}