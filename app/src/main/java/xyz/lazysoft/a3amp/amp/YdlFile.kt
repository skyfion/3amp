package xyz.lazysoft.a3amp.amp

import android.content.res.Resources
import xyz.lazysoft.a3amp.R
import java.io.BufferedInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

class YdlFile(inputStream: InputStream) {

    private var data: ByteArray

    init {
        val bufferedInputStream = BufferedInputStream(inputStream)
        data = bufferedInputStream.readBytes()
    }

    private fun isValid(): Boolean {
        return errorReason() == null
    }

    fun errorReason(): String? {
        return when {
            data.size != Constants.YDL_FILE_SIZE -> "This is not ydl format"
//                Resources.getSystem().getString(R.string.is_not_ydl_file)
//            data.slice(IntRange(0, 3)).toByteArray().toString(Charset.forName("US-ASCII"))
//                    == Constants.YDL_HEADER_TEXT ->
//                Resources.getSystem().getString(R.string.is_not_ydl_file) // todo
            else -> null
        }
    }

    fun presetData(): List<DataPreset>? {
        if (!isValid()) return null
        val result = ArrayList<DataPreset>()
        val pData = data.slice(IntRange(Constants.YDL_HEAD, Constants.YDL_FILE_SIZE - 1))
        assert(pData.size == Constants.YDL_FILE_SIZE - Constants.YDL_HEAD)
        for (i in 0 until Constants.YDL_PRESET_COUNT) {
            val shift = i * Constants.YDL_PRESET_BODY_SIZE
            val pBytes = pData.subList(shift, shift + Constants.YDL_PRESET_BODY_SIZE)
            val body = pBytes.subList(Constants.YDL_NAME, Constants.YDL_NAME + Constants.YDL_DATA)
                    .toByteArray()
            val name = pBytes.subList(0, Constants.YDL_NAME)
                    .filter { it != 0x00.toByte() }
                    .toByteArray()
                    .toString(Charset.forName("US-ASCII"))
            val id = getAmpModel(pBytes.subList(Constants.YDL_NAME + Constants.YDL_DATA, Constants.YDL_PRESET_BODY_SIZE))
            result.add(DataPreset(name, id, body))
        }

        return result
    }

    private fun getAmpModel(m: List<Byte>): AmpModel? {
        return when (m.sum()) {
            0 -> AmpModel.THR10
            1 -> AmpModel.THR5A
            2 -> AmpModel.THR10X
            3 -> AmpModel.THR10C
            else -> null
        }
    }

}

data class DataPreset(val name: String, val model: AmpModel?, val data: ByteArray) {

    private val emptyData = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50, 50, 50,
            50, 50, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            127, 0, 53, 29, 34, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, 0, 30, 70, 46, 93, 96, 0,
            21, 18, 0, 0, 0, 0, 0, 0, 127, 2, 0, 27, 0, 1, 0, 97, 23, 47, 4, 9, 73, 0, 0, 0, 127,
            0, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0).map { it.toByte() }

    fun isInit(): Boolean {
        return data.contentEquals(emptyData.toByteArray())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataPreset

        if (name != other.name) return false
        if (model != other.model) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + model.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
