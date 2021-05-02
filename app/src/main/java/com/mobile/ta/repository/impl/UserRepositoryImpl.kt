package com.mobile.ta.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.model.course.chapter.assignment.UserAssignmentAnswer
import com.mobile.ta.model.course.chapter.assignment.UserSubmittedAssignment
import com.mobile.ta.model.course.chapter.assignment.mapToFirebaseData
import com.mobile.ta.model.status.Status
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.utils.exists
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.mapper.UserSubmittedAssignmentMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class UserRepositoryImpl @Inject constructor(
    db: FirebaseFirestore
) : UserRepository {
    private val users = db.collection(CollectionConstants.USER_COLLECTION)

    // TODO: Change document path to logged in user ID
    override suspend fun submitQuestionResult(
        userAssignmentAnswer: UserAssignmentAnswer,
        courseId: String,
        chapterId: String
    ): Status<Boolean> {
        return users.document("l1CLTummIoarBY3Wb3FY")
            .collection(CollectionConstants.COURSE_COLLECTION).document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION).document(chapterId)
            .collection(CollectionConstants.QUESTION_COLLECTION).document(userAssignmentAnswer.id)
            .set(userAssignmentAnswer)
            .fetchData()
    }

    override suspend fun updateCorrectAnswerCount(
        userSubmittedAssignment: UserSubmittedAssignment,
        courseId: String,
        chapterId: String
    ): Status<Boolean> {
        return users.document("l1CLTummIoarBY3Wb3FY")
            .collection(CollectionConstants.COURSE_COLLECTION).document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION).document(chapterId)
            .update(userSubmittedAssignment.mapToFirebaseData())
            .fetchData()
    }

    override suspend fun resetSubmittedChapter(courseId: String, chapterId: String): Status<Boolean> {
        return users.document("l1CLTummIoarBY3Wb3FY")
            .collection(CollectionConstants.COURSE_COLLECTION).document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION).document(chapterId)
            .delete()
            .fetchData()
    }

    override suspend fun getSubmittedChapter(courseId: String, chapterId: String): Status<UserSubmittedAssignment> {
        return users.document("l1CLTummIoarBY3Wb3FY")
            .collection(CollectionConstants.COURSE_COLLECTION).document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION).document(chapterId)
            .fetchData(UserSubmittedAssignmentMapper::mapToUserSubmittedAssignment)
    }


    override suspend fun getIfSubmittedBefore(courseId: String, chapterId: String): Status<Boolean> {
        return users.document("l1CLTummIoarBY3Wb3FY")
            .collection(CollectionConstants.COURSE_COLLECTION).document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION).document(chapterId)
            .exists()
    }

    override suspend fun createNewSubmittedAssignment(
        courseId: String,
        chapterId: String
    ): Status<Boolean> {
        return users.document("l1CLTummIoarBY3Wb3FY")
            .collection(CollectionConstants.COURSE_COLLECTION).document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION).document(chapterId)
            .set(mapOf(
                "exists" to true
            ))
            .fetchData()
    }
}