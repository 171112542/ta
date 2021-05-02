package com.mobile.ta.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.ta.model.course.information.Chapter
import com.mobile.ta.model.course.information.Course
import com.mobile.ta.utils.wrapper.status.Status

interface CourseRepository {
    suspend fun getCourse(courseId: String): Status<Course>
    suspend fun getChapter(courseId: String, chapterId: String): Status<Chapter>
    suspend fun getChapters(courseId: String): Status<List<Chapter>>
    suspend fun getNthChapter(index: Int): QuerySnapshot?
    suspend fun getChapterById(chapterId: String): DocumentSnapshot
    suspend fun getQuestions(chapterId: String): QuerySnapshot
}