package com.mobile.ta.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.status.Status
import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.mapper.ChapterMapper
import com.mobile.ta.utils.mapper.CourseMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class CourseRepositoryImpl @Inject constructor(
    db: FirebaseFirestore
) : CourseRepository {
    private val courses = db.collection(CollectionConstants.COURSE_COLLECTION)

    override suspend fun searchCourse(courseTitle: String): Status<MutableList<Course>> {
        return courses
            .whereGreaterThanOrEqualTo(CourseMapper.CANONICAL_TITLE_FIELD, courseTitle)
            .whereLessThanOrEqualTo(
                CourseMapper.CANONICAL_TITLE_FIELD,
                courseTitle.substring(0, courseTitle.lastIndex) +
                    courseTitle.last().plus(1)
            )
            .fetchData(CourseMapper::mapToCourses)
    }

    override suspend fun getAllCourses(): Status<MutableList<Course>> {
        return courses
            .fetchData(CourseMapper::mapToCourses)
    }
}