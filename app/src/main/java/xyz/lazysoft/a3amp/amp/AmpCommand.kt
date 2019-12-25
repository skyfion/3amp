package xyz.lazysoft.a3amp.amp

import xyz.lazysoft.a3amp.amp.Constants.Companion.SEND_CMD

class AmpCommand(val data: ByteArray) {
    val id = data[SEND_CMD.size - 1].toInt()

    val value: Int
        get() {
            return if (data[8].toInt() == 0x00) {
                data[9].toInt()
            } else {
                Utils.paramToInt(data.sliceArray(IntRange(8, 9)))
            }
        }

    companion object {
        fun newInstance(data: ByteArray?): AmpCommand? {
            if (data != null && data.size > 9 &&
                    data.slice(IntRange(0, 6)).toByteArray().contentEquals(SEND_CMD)) {
                return AmpCommand(data)
            }
            return null
        }
    }
}