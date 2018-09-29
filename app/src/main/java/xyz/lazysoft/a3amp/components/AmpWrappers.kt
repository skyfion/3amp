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

class AmpKnobWrapper(val knob: Knob) : AmpKnob {
    override fun setOnStateChanged(function: (value: Int) -> Unit) {
        knob.setOnStateChanged(function)
    }

    override var state: Int
        get() = knob.state
        set(value) {
            knob.state = value
        }

}

@SuppressLint("Registered")
class AmpSpinnerWrapper(val s: Spinner) : Activity(), AdapterView.OnItemSelectedListener, AmpSpinner {

    private var onSelectFunction: ((pos: Int) -> Unit)? = null

    override fun setOnSelectItem(function: (pos: Int) -> Unit) {
        onSelectFunction = function
    }

    override var pos: Int
        get() = s.selectedItemPosition
        set(value) {
            runOnUiThread { s.setSelection(value) }
        }

    init {
        s.onItemSelectedListener = this
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        onSelectFunction?.invoke(p2)
    }

}

@SuppressLint("Registered")
class AmpCarouselWrapper(val carousel: CarouselPicker) : Activity(), AmpSpinner, ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    init {
        carousel.addOnPageChangeListener(this)
    }

    private var onSelectFunction: ((pos: Int) -> Unit)? = null

    override fun setOnSelectItem(function: (pos: Int) -> Unit) {
        onSelectFunction = function

    }

    override var pos: Int
        get() = carousel.currentItem
        set(value) { runOnUiThread {carousel.currentItem = value} }

    override fun onPageSelected(position: Int) {
         onSelectFunction?.invoke(position)
    }
}

class AmpSwitchWrapper(val sw: Switch) : AmpSwitch {
    override fun setOnCheckedChangeListener(function: (isChecked: Boolean) -> Unit) {
        sw.setOnCheckedChangeListener { _, isChecked ->
            function.invoke(isChecked)
        }
    }

    override var isChecked: Boolean
        get() = sw.isChecked
        set(value) {
            sw.isChecked = value
        }

}