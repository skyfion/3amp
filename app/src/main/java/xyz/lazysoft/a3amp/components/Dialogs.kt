package xyz.lazysoft.a3amp.components

import android.content.Context
import android.content.DialogInterface
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.customView
import org.jetbrains.anko.sdk27.coroutines.onClick

object Dialogs {
    fun showInputDialog(ctx: Context, dialogTitle: String, dialogValue: String,
                        callback: (String) -> Unit) {
        lateinit var dialog: DialogInterface
        lateinit var text: EditText
        with(ctx) {
            dialog = alert {
                title = dialogTitle
                isCancelable = true
                customView {
                    verticalLayout {
                        linearLayout {
                            text = editText {
                                hint = "enter name" // todo move to resource
                            }.lparams(width = matchParent)
                            text.text = SpannableStringBuilder(dialogValue)
                        }.lparams(width = matchParent)
                        linearLayout {
                            button("Cancel") {
                                onClick {
                                    dialog.dismiss()
                                }
                            }
                            button("OK") {
                                onClick {
                                    val newTitle = text.text.toString()
                                    if (TextUtils.isEmpty(newTitle)) {
                                        text.error = "Enter name!"
                                    } else {
                                        // callback fn
                                        callback(newTitle)
                                        dialog.dismiss()
                                    }
                                }
                            }
                        }.lparams(width = matchParent)
                    }
                }
            }.show()
        }

    }
}