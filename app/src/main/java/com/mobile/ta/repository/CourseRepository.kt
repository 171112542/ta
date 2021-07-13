package com.mobile.ta.repository

import com.mobile.ta.model.course.Course
import com.mobile.ta.utils.wrapper.status.Status

interface CourseRepository {

    suspend fun searchCourse(courseTitle: String): Status<MutableList<Course>>

    suspend fun getAllCourses(): Status<MutableList<Course>>

    suspend fun getAllCoursesByCreatorId(teacherId: String): Status<MutableList<Course>>

    suspend fun searchCourseWithCreatorId(courseTitle: String, teacherId: String): Status<MutableList<Course>>

    suspend fun getCourseById(courseId: String): Status<Course>

    suspend fun getTotalPublishedCourse(teacherId: String): Status<Int>

    suspend fun updateTotalEnrolledCourse(courseId: String, totalEnrolled: Int): Status<Boolean>
}