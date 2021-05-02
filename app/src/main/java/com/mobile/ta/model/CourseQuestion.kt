package com.mobile.ta.model

import com.google.firebase.firestore.DocumentId

data class CourseQuestion(
    @DocumentId
    val id: String,
    val question: String,
    val choices: List<String>,
    val correctAnswer: Int,
    val explanation: String,
    val order: Int
) {
    constructor() : this("", "", listOf(), 0, "", 0)
}