package com.mobile.ta.model.course.chapter.assignment

import com.google.firebase.firestore.DocumentId

data class UserAssignmentAnswer(
    @DocumentId
    val id: String,
    val question: String,
    val selectedAnswer: Int,
    val correctAnswer: Int,
    val order: Int
)

fun UserAssignmentAnswer.mapToFirebaseData(): Map<String, Any> =
    mapOf(

    )