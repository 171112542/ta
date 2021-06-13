package com.mobile.ta.model.course

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.information.Creator
import com.mobile.ta.model.course.information.LevelTag
import com.mobile.ta.model.course.information.TypeTag

data class Course(
    @DocumentId
    val id: String,
    val chapterSummaryList: List<ChapterSummary>,
    val title: String,
    val imageUrl: String,
    val description: String,
    val creator: Creator,
    val level: LevelTag,
    val type: TypeTag,
    val preRequisiteCourse: List<String>,
    val relatedCourse: List<String>,
    var totalEnrolled: Int,
    val updatedAt: Timestamp? = null,
    val enrollmentKey: String? = null
) {
    constructor() : this(
        "",
        listOf(),
        "",
        "",
        "",
        Creator(),
        LevelTag.JUNIOR_ONE,
        TypeTag.BIOLOGY,
        listOf(),
        listOf(),
        0
    )
}
