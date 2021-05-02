package com.mobile.ta.repository

import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.status.Status

interface CourseRepository {
    suspend fun searchCourse(courseTitle: String): Status<MutableList<Course>>

    suspend fun getAllCourses(): Status<MutableList<Course>>
}