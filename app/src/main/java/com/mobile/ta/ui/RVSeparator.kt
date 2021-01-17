package com.mobile.ta.ui

import android.content.Context
import android.content.res.Resources
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.R

object RVSeparator {
    fun getSpaceSeparator(context: Context, orientation: Int, resources: Resources): DividerItemDecoration {
        return DividerItemDecoration(context, orientation).apply {
            val drawable = ResourcesCompat.getDrawable(
                resources,
                R.drawable.rv_space_separator, null
            ) ?: return@apply
            setDrawable(drawable)
        }
    }
}