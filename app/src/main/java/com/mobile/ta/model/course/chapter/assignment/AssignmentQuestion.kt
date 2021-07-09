package com.mobile.ta.model.course.chapter.assignment

import com.google.firebase.firestore.DocumentId

data class AssignmentQuestion(
    val question: String,
    val choices: List<String>,
    val correctAnswer: Int,
    val explanation: String,
    val order: Int
) {
    constructor() : this("", listOf(), 0, "", 0)
}