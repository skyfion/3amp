package xyz.lazysoft.a3amp.amp

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.net.URI
import java.nio.ByteBuffer

object Utils {

    fun byteArrayOf(vararg a: Int): ByteArray {
        return if (a.isNotEmpty())
            a.map { i -> i.toByte() }.toByteArray()
        else
            ByteArray(0)
    }

    fun paramToInt(param: ByteArray): Int {
        return ByteBuffer.wrap(param).short.toInt()
    }

    fun intToParam(value: Int): ByteArray {
        return ByteBuffer.wrap(ByteArray(4))
                .putInt(value)
                .array().drop(2).toByteArray()
    }

    fun getFileName(context: Context, uri: Uri): String {

        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        if (cursor != null) {
                            return cursor.getString(0)
                        } // cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    }
                }
            }
        }

        return uri.path!!.substring(uri.path!!.lastIndexOf('/') + 1)
    }
}