package com.mobile.ta.model.user.course.chapter.assignment

import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.utils.mapper.UserSubmittedAssignmentMapper

data class UserSubmittedAssignment(
    val title: String,
    val type: ChapterType,
    val correctAnswerCount: Int,
    val totalAnswerCount: Int
) {
    constructor() : this("", ChapterType.CONTENT, -1, -1)
}

fun UserSubmittedAssignment.mapToFirebaseData(): Map<String, Any> =
    mapOf(
        UserSubmittedAssignmentMapper.TITLE_FIELD to this.title,
        UserSubmittedAssignmentMapper.TYPE_FIELD to this.type,
        UserSubmittedAssignmentMapper.CORRECT_ANSWER_COUNT_FIELD to this.correctAnswerCount,
        UserSubmittedAssignmentMapper.TOTAL_ANSWER_COUNT_FIELD to this.totalAnswerCount
    )

fun UserSubmittedAssignment.isFinishedBefore(): Boolean =
    this.totalAnswerCount != -1