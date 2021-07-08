package com.mobile.ta.model.course.chapter.assignment

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.mobile.ta.model.course.chapter.ChapterType

data class Assignment(
    @DocumentId
    val id: String,
    val createdAt: Timestamp,
    val order: Int,
    val passingGrade: Int,
    val questions: List<AssignmentQuestion>,
    val title: String,
    val type: ChapterType,
    val typeOrder: Int,
    val updatedAt: Timestamp
) {
    constructor(): this(
        "",
        Timestamp.now(),
        -1,
        0,
        listOf(),
        "",
        ChapterType.PRACTICE,
        -1,
        Timestamp.now()
    )
}