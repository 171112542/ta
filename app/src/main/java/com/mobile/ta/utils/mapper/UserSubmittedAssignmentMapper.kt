package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.mobile.ta.model.user.course.chapter.assignment.UserSubmittedAssignment

object UserSubmittedAssignmentMapper {
    const val TITLE_FIELD = "title"
    const val TYPE_FIELD = "type"
    const val SCORE_FIELD = "score"
    const val PASSING_GRADE_FIELD = "passingGrade"
    const val PASSED_FIELD = "passed"
    const val FINISHED_FIELD = "finished"

    fun mapToUserSubmittedAssignment(snapshot: DocumentSnapshot): UserSubmittedAssignment {
        return snapshot.toObject(UserSubmittedAssignment::class.java) ?: UserSubmittedAssignment()
    }
}