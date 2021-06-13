package com.mobile.ta.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.model.testing.TimeSpent
import com.mobile.ta.repository.TestingTimerRepository
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.wrapper.status.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

/**
 * A Repository that contains the functions to save the timer
 * of when the user starts navigating to a fragment and to save
 * the timer of when the user finishes interacting with the fragment
 */
@ExperimentalCoroutinesApi
class TestingTimerRepositoryImpl @Inject constructor(
    val database: FirebaseFirestore
): TestingTimerRepository {
    private val userCollection by lazy {
        database.collection(CollectionConstants.USER_COLLECTION)
    }

    override suspend fun saveTimeSpent(
        userId: String,
        courseId: String,
        chapterId: String,
        timeSpent: TimeSpent
    ): Status<Boolean> =
        userCollection.document(userId)
            .collection(CollectionConstants.COURSE_COLLECTION)
            .document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION)
            .document(chapterId)
            .collection(CollectionConstants.TIME_COLLECTION)
            .add(timeSpent)
            .fetchData()
}