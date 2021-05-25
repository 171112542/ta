package com.mobile.ta.repository

import androidx.lifecycle.LiveData
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion
import com.mobile.ta.utils.wrapper.status.Status

interface ChapterRepository {
    suspend fun getChapters(courseId: String): Status<MutableList<Chapter>>

    suspend fun getNthChapter(index: Int): Status<MutableList<Course>>

    suspend fun getChapterById(courseId: String, chapterId: String): Status<Chapter>

    suspend fun getQuestions(
        courseId: String,
        chapterId: String
    ): Status<MutableList<AssignmentQuestion>>
}