package com.mobile.ta.repository

import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.user.course.UserCourse
import com.mobile.ta.utils.wrapper.status.Status

interface UserCourseRepository {
    suspend fun getUserCourses(userId: String, isFinished: Boolean): Status<MutableList<UserCourse>>
    suspend fun getUserCourse(userId: String, courseId: String): Status<UserCourse>
    suspend fun addUserCourse(
        userId: String,
        courseId: String,
        data: HashMap<String, Any?>
    ): Status<Boolean>

    suspend fun updateLastAccessedChapter(
        userId: String,
        courseId: String,
        lastAccessedChapter: ChapterSummary
    ): Status<Boolean>

    suspend fun updateFinishedCourse(
        userId: String,
        courseId: String,
        data: HashMap<String, Any?>
    ): Status<Boolean>
}