package com.mobile.ta.model

import android.graphics.Bitmap
import java.util.*

data class User(
    val name: String,
    val isFirstTimeLogin: Boolean,
    val email: String,
    val phone: String,
    val photo: Bitmap?,
    val dob: Date?,
    val bio: String,
)