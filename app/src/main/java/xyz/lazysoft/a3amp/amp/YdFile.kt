package xyz.lazysoft.a3amp.amp

import java.io.BufferedInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

class YdFile(inputStream: InputStream) {

    private var data: ByteArray = BufferedInputStream(inputStream).readBytes()

    private var type: Type? = when {
        data.size == Constants.YDL_FILE_SIZE -> Type.YDL
        data.size == Constants.YDP_FILE_SIZE -> Type.YDP
        else -> null
    }

    private fun getHeader(): String {
        return DataPreset.bytesToString(data.slice(IntRange(0, Constants.YDL_HEAD - 1)))
    }

    private fun isValid(): Boolean {
        return errorReason() == null
    }

    fun errorReason(): String? {
        return when {
            type == null -> "This is not ydl or ydp format"
            else -> null
        }
    }

    enum class Type {
        YDL, YDP
    }

    fun presetData(): List<DataPreset>? {
        if (!isValid()) return null
        val result = ArrayList<DataPreset>()
        if (type == Type.YDL) {
            val pData = data.slice(IntRange(Constants.YDL_HEAD, Constants.YDL_FILE_SIZE - 1))
            for (i in 0 until Constants.YDL_PRESET_COUNT) {
                val shift = i * Constants.YDL_PRESET_BODY_SIZE
                val pBytes = pData.subList(shift, shift + Constants.YDL_PRESET_BODY_SIZE)
                result.add(DataPreset.toDataPreset(pBytes))
            }
        } else {
            val pData = data.slice(IntRange(Constants.YDP_HEAD, Constants.YDP_FILE_SIZE - 1))
            result.add(DataPreset.toDataPreset(pData))
        }

        return result
    }

}

data class DataPreset(val name: String, val model: AmpModel?, val data: ByteArray) {

    fun isInit(): Boolean {
        return name == "Init"
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

    companion object {
        fun bytesToString(data: List<Byte>): String {
            return data.filter { it != 0x00.toByte() }
                    .toByteArray()
                    .toString(Charset.forName("US-ASCII"))
                    .trim()
        }

        fun toDataPreset(pBytes: List<Byte>): DataPreset {
            val body = pBytes.subList(Constants.YDL_NAME, Constants.YDL_NAME + Constants.YDL_DATA)
                    .toByteArray()
            val name = bytesToString(pBytes.subList(0, Constants.YDL_NAME))

            val idData = pBytes.subList(Constants.YDL_NAME + Constants.YDL_DATA,
                    Constants.YDL_PRESET_BODY_SIZE)
            val id = getAmpModel(idData)
            return DataPreset(name, id, body)
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
}
