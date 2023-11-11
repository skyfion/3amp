package xyz.lazysoft.a3amp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import it.beppi.knoblibrary.Knob
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.runners.MockitoJUnitRunner
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.amp.Constants
import xyz.lazysoft.a3amp.components.wrappers.AmpKnobWrapper
import xyz.lazysoft.a3amp.midi.SysExMidiManager

@RunWith(MockitoJUnitRunner::class)
class AmpTest {
//    private val midiManager = StubMidiManager()
//    private val amp = Amp(midiManager)
//
//    init {
//   //     amp.addKnob(, Constants.K_GAIN)
//
//    }
//
//    @Mock
//    private lateinit var mockContext: Context
//
//    class StubMidiManager : SysExMidiManager {
//
//        override var sysExtListeners: ArrayList<(ByteArray?) -> Unit> = ArrayList()
//
//        override fun sendSysExCmd(cmd: ByteArray) {
//
//        }
//    }
//
//    fun makeKnobCommand(id: Int, param: Int): ByteArray {
//        return Constants.SEND_CMD + id.toByte() + 0x0 + param.toByte() + Constants.END
//    }
//
//    @Test
//    fun knobTest() {
//        val knobGain = Knob(mockContext)
//        knobGain.numberOfStates = 100
//        val knob = AmpKnobWrapper(knobGain, null)
//
//        amp.addKnob(knob, Constants.K_GAIN)
//        midiManager.onMidiSystemExclusive(makeKnobCommand(Constants.K_GAIN, 50))
//        Assert.assertEquals(knob.state, 50)
//        midiManager.onMidiSystemExclusive(makeKnobCommand(Constants.K_GAIN, 0))
//        Assert.assertEquals(knob.state, 0)
//        midiManager.onMidiSystemExclusive(makeKnobCommand(Constants.K_GAIN, 100))
//        Assert.assertEquals(knob.state, 100)
//    }
}