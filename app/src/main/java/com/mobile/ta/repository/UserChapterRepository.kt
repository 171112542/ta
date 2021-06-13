package com.mobile.ta.repository

import com.mobile.ta.model.user.course.chapter.UserChapter
import com.mobile.ta.utils.wrapper.status.Status

interface UserChapterRepository {
    suspend fun getFinishedUserChapters(userId: String, courseId: String): Status<MutableList<UserChapter>>
    suspend fun addUserChapter(
        userId: String,
        courseId: String,
        chapterId: String,
        data: HashMap<String, Any>
    ): Status<Boolean>
}