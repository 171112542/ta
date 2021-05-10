package com.mobile.ta.utils

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

object ImageUtil {

    fun loadImage(context: Context, imageUrl: String, imageView: ImageView) {
        Glide.with(context)
            .load(imageUrl)
            .centerCrop()
            .into(imageView)
    }

    fun loadImageWithPlaceholder(
        context: Context,
        imageUrl: String,
        imageView: ImageView,
        @DrawableRes placeholder: Int
    ) {
        Glide.with(context)
            .load(imageUrl)
            .centerCrop()
            .placeholder(placeholder)
            .error(placeholder)
            .into(imageView)
    }
}