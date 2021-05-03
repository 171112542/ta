package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.mobile.ta.model.user.course.chapter.assignment.UserSubmittedAssignment

object UserSubmittedAssignmentMapper {
    const val TITLE_FIELD = "title"
    const val TYPE_FIELD = "type"
    const val CORRECT_ANSWER_COUNT_FIELD = "correctAnswerCount"
    const val TOTAL_ANSWER_COUNT_FIELD = "totalAnswerCount"

    fun mapToUserSubmittedAssignment(snapshot: DocumentSnapshot): UserSubmittedAssignment {
        return snapshot.toObject(UserSubmittedAssignment::class.java) ?: UserSubmittedAssignment()
    }
}