package com.mobile.ta.model

import com.google.firebase.firestore.DocumentId

data class CourseQuestionAnswer(
    @DocumentId
    val id: String,
    val question: String,
    val selectedAnswer: Int,
    val correctAnswer: Int,
    val order: Int
)