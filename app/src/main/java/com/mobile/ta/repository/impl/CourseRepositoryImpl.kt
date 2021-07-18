package com.mobile.ta.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mobile.ta.config.CollectionConstants.COURSE_COLLECTION
import com.mobile.ta.model.course.Course
import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.fetchDataCount
import com.mobile.ta.utils.mapper.CourseMapper
import com.mobile.ta.utils.mapper.CourseMapper.TOTAL_ENROLLED_FIELD
import com.mobile.ta.utils.wrapper.status.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class CourseRepositoryImpl @Inject constructor(
    database: FirebaseFirestore
) : CourseRepository {
    private val courseCollection =
        database.collection(COURSE_COLLECTION)

    override suspend fun getCourseById(courseId: String): Status<Course> {
        return courseCollection.document(courseId).fetchData(CourseMapper::mapToCourse)
    }

    override suspend fun getTotalPublishedCourse(teacherId: String): Status<Int> {
        return courseCollection
            .whereEqualTo(CourseMapper.CREATOR_ID, teacherId)
            .whereEqualTo(CourseMapper.ARCHIVE_FIELD, false)
            .fetchDataCount()
    }

    override suspend fun searchCourse(courseTitle: String): Status<MutableList<Course>> {
        return courseCollection
            .whereGreaterThanOrEqualTo(CourseMapper.CANONICAL_TITLE_FIELD, courseTitle)
            .whereLessThanOrEqualTo(
                CourseMapper.CANONICAL_TITLE_FIELD,
                courseTitle.substring(0, courseTitle.lastIndex) +
                        courseTitle.last().plus(1)
            )
            .fetchData(CourseMapper::mapToCourses)
    }

    override suspend fun getAllCourses(): Status<MutableList<Course>> {
        return courseCollection
            .whereEqualTo(CourseMapper.ARCHIVE_FIELD, false)
            .orderBy(CourseMapper.CANONICAL_TITLE_FIELD, Query.Direction.ASCENDING)
            .fetchData(CourseMapper::mapToCourses)
    }

    override suspend fun getAllCoursesByCreatorId(teacherId: String): Status<MutableList<Course>> {
        return courseCollection
            .whereEqualTo(CourseMapper.CREATOR_ID, teacherId)
            .orderBy(CourseMapper.CANONICAL_TITLE_FIELD, Query.Direction.ASCENDING)
            .fetchData(CourseMapper::mapToCourses)
    }

    override suspend fun updateTotalEnrolledCourse(
        courseId: String,
        totalEnrolled: Int
    ): Status<Boolean> {
        return courseCollection.document(courseId).update(
            hashMapOf<String, Any>(
                TOTAL_ENROLLED_FIELD to totalEnrolled
            )
        ).fetchData()
    }
}