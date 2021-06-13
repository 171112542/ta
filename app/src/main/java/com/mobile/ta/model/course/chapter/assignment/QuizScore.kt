package com.mobile.ta.model.course.chapter.assignment

import com.google.firebase.Timestamp

data class QuizScore(
    val score: Int,
    val userId: String,
    val userEmail: String,
    val submittedDate: Timestamp
) {
    constructor(): this(-1, "", "", Timestamp.now())
}