package com.mobile.ta.model

import java.util.*

data class Feedback(
    val id: String,
    val feedbackType: String,
    val description: String,
    val createdAt: Date
)