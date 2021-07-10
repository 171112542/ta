package com.mobile.ta.utils.view

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

    fun <T : Any> loadImage(context: Context, image: T, imageView: ImageView) {
        Glide.with(context).load(image).centerCrop().into(imageView)
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