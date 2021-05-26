package com.mobile.ta.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.ta.config.CollectionConstants.CHAPTER_COLLECTION
import com.mobile.ta.config.CollectionConstants.COURSE_COLLECTION
import com.mobile.ta.config.CollectionConstants.USER_COLLECTION
import com.mobile.ta.model.user.course.chapter.UserChapter
import com.mobile.ta.repository.UserChapterRepository
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.mapper.UserChapterMapper
import com.mobile.ta.utils.wrapper.status.Status
import javax.inject.Inject

class UserChapterRepositoryImpl @Inject constructor(private val database: FirebaseFirestore) :
    UserChapterRepository {
    private val userCollection = database.collection(USER_COLLECTION)
    override suspend fun getUserChapters(
        userId: String,
        courseId: String
    ): Status<MutableList<UserChapter>> {
        return userCollection.document(userId).collection(COURSE_COLLECTION).document(courseId)
            .collection(
                CHAPTER_COLLECTION
            ).fetchData(UserChapterMapper::mapToUserChapters)
    }

    override suspend fun addUserChapter(
        userId: String,
        courseId: String,
        chapterId: String,
        data: HashMap<String, Any>
    ): Status<Boolean> {
        return userCollection.document(userId).collection(COURSE_COLLECTION).document(courseId)
            .collection(
                CHAPTER_COLLECTION
            ).document(chapterId).set(data).fetchData()
    }
}