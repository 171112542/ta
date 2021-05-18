package com.mobile.ta.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mobile.ta.config.CollectionConstants.COURSE_COLLECTION
import com.mobile.ta.config.CollectionConstants.USER_COLLECTION
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.user.course.UserCourse
import com.mobile.ta.repository.UserCourseRepository
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.mapper.UserCourseMapper
import com.mobile.ta.utils.mapper.UserCourseMapper.FINISHED_FIELD
import com.mobile.ta.utils.mapper.UserCourseMapper.LAST_ACCESSED_CHAPTER_FIELD
import com.mobile.ta.utils.mapper.UserCourseMapper.toHashMap
import com.mobile.ta.utils.wrapper.status.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class UserCourseRepositoryImpl @Inject constructor(private val database: FirebaseFirestore) :
    UserCourseRepository {
    private val userCollection = database.collection(USER_COLLECTION)

    override suspend fun getUserCourses(
        userId: String,
        isFinished: Boolean
    ): Status<MutableList<UserCourse>> {
        return userCollection.document(userId).collection(COURSE_COLLECTION)
            .whereEqualTo(FINISHED_FIELD, isFinished)
            .fetchData(UserCourseMapper::mapToUserCourses)
    }

    override suspend fun getUserCourse(userId: String, courseId: String): Status<UserCourse> {
        return userCollection.document(userId).collection(COURSE_COLLECTION)
            .document(courseId)
            .fetchData(UserCourseMapper::mapToUserCourse)
    }

    override suspend fun addUserCourse(
        userId: String,
        courseId: String,
        data: HashMap<String, Any?>
    ): Status<Boolean> {
        return userCollection.document(userId).collection(COURSE_COLLECTION).document(courseId)
            .set(data, SetOptions.mergeFields(FINISHED_FIELD, LAST_ACCESSED_CHAPTER_FIELD))
            .fetchData()
    }

    override suspend fun updateLastAccessedChapter(
        userId: String,
        courseId: String,
        lastAccessedChapter: ChapterSummary
    ): Status<Boolean> {
        return userCollection.document(userId).collection(COURSE_COLLECTION).document(courseId)
            .set(
                UserCourse().toHashMap(lastAccessedChapter),
                SetOptions.mergeFields(LAST_ACCESSED_CHAPTER_FIELD)
            ).fetchData()
    }
}