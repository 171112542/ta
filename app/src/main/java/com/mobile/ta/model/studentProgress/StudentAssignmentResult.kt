package com.mobile.ta.model.studentProgress

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion

data class StudentAssignmentResult(
    @DocumentId
    val id: String,
    val score: Int,
    val submittedDate: Timestamp,
    val passingGrade: Int,
    val title: String,
    val type: ChapterType,
    val questions: List<SubmittedAnswer>
) {
    constructor(): this(
        "",
        -1,
        Timestamp.now(),
        -1,
        "",
        ChapterType.PRACTICE,
        listOf()
    )
}