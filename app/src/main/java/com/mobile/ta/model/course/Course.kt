package com.mobile.ta.model.course

import com.google.firebase.firestore.DocumentId
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.information.Creator
import com.mobile.ta.model.course.information.LevelTag
import com.mobile.ta.model.course.information.RelatedCourse
import com.mobile.ta.model.course.information.TypeTag

data class Course(
    @DocumentId
    val id: String? = null,
    val chapterSummaryList: List<ChapterSummary>? = null,
    val title: String? = null,
    val imageUrl: String? = null,
    val description: String? = null,
    val creator: Creator? = null,
    val level: LevelTag? = null,
    val type: TypeTag? = null,
    val prerequisiteCourse: List<RelatedCourse>? = null,
    val relatedCourse: List<RelatedCourse>? = null,
    val totalStudentEnrolled: Int? = null
)