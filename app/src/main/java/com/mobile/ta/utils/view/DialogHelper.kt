package com.mobile.ta.utils.view

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.mobile.ta.R
import com.mobile.ta.utils.orFalse

object DialogHelper {

    fun createLoadingDialog(context: Context): Dialog {
        return Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.layout_loading_dialog)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
    }

    fun showDialog(dialog: Dialog?) {
        if (dialog?.isShowing.orFalse().not()) {
            dialog?.show()
        }
    }

    fun dismissDialog(dialog: Dialog?) {
        if (dialog?.isShowing.orFalse()) {
            dialog?.dismiss()
        }
    }
}