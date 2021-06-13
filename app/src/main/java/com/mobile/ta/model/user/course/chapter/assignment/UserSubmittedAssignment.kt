package com.mobile.ta.model.user.course.chapter.assignment

import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.utils.mapper.UserSubmittedAssignmentMapper

data class UserSubmittedAssignment(
    val title: String,
    val type: ChapterType,
    val score: Int,
    val passingGrade: Int,
    val passed: Boolean
) {
    constructor() : this("", ChapterType.CONTENT, -1, -1, false)
}

fun UserSubmittedAssignment.mapToFirebaseData(): Map<String, Any> =
    mapOf(
        UserSubmittedAssignmentMapper.TITLE_FIELD to this.title,
        UserSubmittedAssignmentMapper.TYPE_FIELD to this.type,
        UserSubmittedAssignmentMapper.SCORE_FIELD to this.score,
        UserSubmittedAssignmentMapper.PASSING_GRADE_FIELD to this.passingGrade,
        UserSubmittedAssignmentMapper.PASSED_FIELD to this.passed
    )

fun UserSubmittedAssignment.isFinishedBefore(): Boolean =
    this.score != -1