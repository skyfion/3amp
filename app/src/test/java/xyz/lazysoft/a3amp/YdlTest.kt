package xyz.lazysoft.a3amp

import org.junit.Assert
import org.junit.Test
import xyz.lazysoft.a3amp.amp.*
import xyz.lazysoft.a3amp.midi.SysExMidiManager

class YdlTest {
    private val dump: Array<Byte> = arrayOf(-16, 67, 125, 0, 2, 12, 68, 84, 65, 49, 65, 108, 108, 80, 0, 0, 127, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 48, 34, 31, 74, 65, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, 1, 14, 48, 36, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 70, 10, 31, 32, 0, 21, 19, 0, 0, 0, 0, 0, 0, 0, 2, 0, 16, 0, 70, 0, 95, 21, 112, 8, 13, 61, 0, 0, 0, 0, 0, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, -9)

    @Test
    fun parseYdlFile() {
        val inputStream =  ClassLoader.getSystemClassLoader().getResourceAsStream("test.ydl")
        val presets = YdlFile(inputStream).presetData()!!
        val preset = presets[0]
        Assert.assertEquals(preset.name, "MODERN HM backing")
        Assert.assertEquals(preset.model, AmpModel.THR10)
    }

    @Test
    fun parseDumpTest() {
       val cmds = YdlDataConverter.dumpTo(dump.toByteArray())
        Assert.assertEquals(cmds.isNotEmpty(), true)
        Assert.assertEquals(cmds.size, 31)
    }

    @Test
    fun writeDumpState() {
        val state = PresetDump(Constants.initPresetDump.toByteArray())
        val reverbTime = Constants.REVERB_TIME
        state.writeDump(reverbTime, Pair(0, 99))

        val index = Constants.DUMP_MAP[reverbTime] as Int
        Assert.assertEquals(state.get(index), 99.toByte())

        val gain = Utils.paramToInt(state.getValueById(Constants.K_GAIN)!!)
        Assert.assertEquals(gain, 50)
    }

    class StubMindiManager : SysExMidiManager {
        override var sysExtListeners: ArrayList<(ByteArray?) -> Unit>
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
            set(value) {}
        override var sendSysExtListeners: ArrayList<(ByteArray?) -> Unit>
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
            set(value) {}

    }

}