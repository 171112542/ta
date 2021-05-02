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
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.mobile.ta.model.user.User
import com.mobile.ta.utils.fetchDataWithResult
import com.mobile.ta.utils.mapper.UserMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class UserRepositoryImpl @Inject constructor(
    db: FirebaseFirestore
) : UserRepository {
    private val users = db.collection(CollectionConstants.USER_COLLECTION)
    private val userCollection by lazy {
        database.collection(CollectionConstants.USER_COLLECTION)
    }

    private val storageReference by lazy {
        storage.reference
    }

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

    override suspend fun getUserById(id: String): Status<User> {
        return userCollection.document(id).fetchData(UserMapper::mapToUser)
    }

    override suspend fun getUserImageUrl(userId: String, imageUri: Uri): Status<Uri> {
        return storageReference
            .child("${CollectionConstants.IMAGES_USERS_STORAGE_PATH}/$userId/${imageUri.lastPathSegment}")
            .downloadUrl.fetchDataWithResult()
    }

    override suspend fun updateUser(user: User): Status<Boolean> {
        return userCollection.document(user.id).update(
            mapOf(
                UserMapper.NAME to user.name,
                UserMapper.BIRTH_DATE to user.birthDate,
                UserMapper.PHOTO to user.photo,
                UserMapper.PHONE_NUMBER to user.phoneNumber,
                UserMapper.BIO to user.bio
            )
        ).fetchData()
    }

    override suspend fun uploadUserImage(userId: String, imageUri: Uri): Status<Boolean> {
        return storageReference
            .child("${CollectionConstants.IMAGES_USERS_STORAGE_PATH}/$userId/${imageUri.lastPathSegment}")
            .putFile(imageUri).fetchData()
    }
}