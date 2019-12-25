package xyz.lazysoft.a3amp

import xyz.lazysoft.a3amp.amp.AmpCommand
import xyz.lazysoft.a3amp.amp.Constants
import xyz.lazysoft.a3amp.amp.Constants.Companion.END
import xyz.lazysoft.a3amp.amp.Constants.Companion.OFF
import xyz.lazysoft.a3amp.amp.Constants.Companion.ON
import xyz.lazysoft.a3amp.amp.Constants.Companion.SEND_CMD
import xyz.lazysoft.a3amp.amp.Utils
import xyz.lazysoft.a3amp.components.wrappers.AmpComponent

class ThrComponents(private val sendFn: (ByteArray) -> Unit) {

    val components: HashMap<Int, AmpComponent<Int>> = HashMap()

    fun receiveCommand(cmd: AmpCommand) {
        components[cmd.id]?.state = cmd.value
    }

    fun addOffSpinner(spinner: AmpComponent<Int>, id: Int, swId: Int): ThrComponents {
        spinner.setOnStateChanged {
            if (it == 0) {
                sendFn.invoke(SEND_CMD + Utils.byteArrayOf(swId, 0x00, OFF, END))
            } else {
                sendFn.invoke(SEND_CMD + Utils.byteArrayOf(swId, 0x00, Constants.ON, END))
                sendFn.invoke(SEND_CMD + Utils.byteArrayOf(id, 0x00, (it - 1), END))
            }
        }
        components[id] = spinner
        components[swId] = ThrSwitcher(spinner)
        return this
    }

    fun addSpinner(spinner: AmpComponent<Int>, id: Int): ThrComponents {
        spinner.setOnStateChanged {
            sendFn.invoke(SEND_CMD + id.toByte() + 0x00 + it.toByte() + END.toByte())
        }
        components[id] = spinner
        return this
    }

    fun addKnob(knob: AmpComponent<Int>, id: Int): ThrComponents {
        knob.setOnStateChanged {
            sendFn.invoke(SEND_CMD + id.toByte() + Utils.intToParam(it) + END.toByte())
        }
        components[id] = knob
        return this
    }

    fun addSwSpinner(carousel: AmpComponent<Int>, swId: Int): ThrComponents {
        carousel.setOnStateChanged {
            val value = if (it == 0) OFF else ON
            sendFn.invoke(SEND_CMD + swId.toByte() + 0x00 + value.toByte() + END.toByte())
        }
        components[swId] = ThrSwitcherWrapper(carousel)
        return this
    }
}

class ThrSwitcherWrapper(private val carousel: AmpComponent<Int>) : AmpComponent<Int> {
    override fun setOnStateChanged(function: (value: Int) -> Unit): AmpComponent<Int> {
        return this
    }

    override var state: Int
        get() = carousel.state
        set(value) {
           carousel.state = if (value == OFF) 0 else 1
        }

}


class ThrSwitcher(private val mainComponent: AmpComponent<Int>) : AmpComponent<Int> {
    override fun setOnStateChanged(function: (value: Int) -> Unit): AmpComponent<Int> {
        return this
    }

    override var state: Int
        get() = mainComponent.state
        set(value) {
            if (value == OFF) {
                mainComponent.state = 0
            }
        }

}