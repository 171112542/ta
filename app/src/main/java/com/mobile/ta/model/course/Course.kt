package com.mobile.ta.model.course

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.information.LevelTag
import com.mobile.ta.model.course.information.TypeTag

data class Course(
    @DocumentId
    val id: String,
    val archive: Boolean,
    val chapterSummaryList: List<ChapterSummary>,
    val title: String,
    val imageUrl: String,
    val description: String,
    val creatorId: String,
    val level: LevelTag,
    val type: TypeTag,
    val prerequisiteCourse: List<String>,
    val relatedCourse: List<String>,
    var totalEnrolled: Int,
    val updatedAt: Timestamp? = null,
    val enrollmentKey: String? = null
) {
    constructor() : this(
        "",
        true,
        listOf(),
        "",
        "",
        "",
        "",
        LevelTag.JUNIOR_ONE,
        TypeTag.BIOLOGY,
        listOf(),
        listOf(),
        0
    )
}
