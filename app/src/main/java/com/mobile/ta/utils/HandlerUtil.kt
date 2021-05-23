package com.mobile.ta.utils

import android.app.Activity
import android.os.Handler
import android.os.Looper

object HandlerUtil {
    private val handler = Handler(Looper.getMainLooper())
    fun runOnUiThread(activity: Activity, block: () -> Unit) {
        handler.post {
            activity.runOnUiThread {
                block.invoke()
            }
        }
    }
}