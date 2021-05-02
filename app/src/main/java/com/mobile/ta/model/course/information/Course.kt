package com.mobile.ta.model.course.information

import com.google.firebase.firestore.DocumentId
import com.mobile.ta.model.LevelTag
import com.mobile.ta.model.TypeTag
import com.mobile.ta.model.course.discussion.DiscussionForum

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