package com.mobile.ta.model.course.chapter.assignment

import com.google.firebase.Timestamp

data class QuizScore(
    val score: Int,
    val submittedDate: Timestamp
) {
    constructor(): this(-1, Timestamp.now())
}