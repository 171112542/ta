package com.mobile.ta.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

object ImageUtil {

    fun loadImage(context: Context, imageUrl: String, imageView: ImageView) {
        Glide.with(context)
            .load(imageUrl)
            .centerCrop()
            .into(imageView)
    }
}