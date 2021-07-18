package com.mobile.ta.model.studentProgress

import com.google.firebase.firestore.DocumentId
import com.mobile.ta.model.course.chapter.ChapterSummary

data class StudentProgress(
    @DocumentId
    val id: String = "",
    val enrolled: Boolean = true,
    val finished: Boolean = false,
    val finishedChapterIds: List<String> = listOf(),
    val lastAccessedChapter: ChapterSummary? = null,
    val course: ShortCourse? = null,
    val student: Student? = null,
    val totalChapterCount: Int = 0,
    val quizDoneCount: Int = 0,
    val averageScore: Float? = null
)