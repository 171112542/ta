package com.mobile.ta.utils

import android.content.Context
import android.util.TypedValue

object ThemeUtil {
    fun getThemeColor(context: Context, resourceId: Int): Int {
        return TypedValue().apply {
            val theme = context.theme
            theme.resolveAttribute(resourceId, this, true)
        }.data
    }
}