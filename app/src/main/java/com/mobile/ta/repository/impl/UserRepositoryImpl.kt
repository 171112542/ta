package com.mobile.ta.repository.impl

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.model.user.User
import com.mobile.ta.model.user.feedback.Feedback
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.mapper.UserMapper
import com.mobile.ta.utils.wrapper.status.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ExperimentalCoroutinesApi
class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    database: FirebaseFirestore,
    storage: FirebaseStorage
) : UserRepository {

    private val userCollection by lazy {
        database.collection(CollectionConstants.USER_COLLECTION)
    }

    private val storageReference by lazy {
        storage.reference
    }

    override suspend fun addUserFeedback(id: String, data: HashMap<String, Any?>): Status<Boolean> {
        return userCollection.document(id)
            .collection(CollectionConstants.FEEDBACK_COLLECTION)
            .add(data)
            .fetchData()
    }

    override suspend fun getUser(): Status<User> {
        return auth.currentUser?.let {
            userCollection.document(it.uid).fetchData(UserMapper::mapToUser)
        } ?: Status.error(null, null)
    }

    override suspend fun getUserFeedbacks(id: String): Status<MutableList<Feedback>> {
        return userCollection.document(id)
            .collection(CollectionConstants.FEEDBACK_COLLECTION)
            .orderBy(UserMapper.CREATED_AT, Query.Direction.DESCENDING)
            .fetchData(UserMapper::mapToUserFeedbacks)
    }

    override suspend fun updateUser(user: User): Status<Boolean> {
        return userCollection.document(user.id).update(
            mapOf(
                UserMapper.NAME to user.name,
                UserMapper.BIRTH_DATE to user.birthDate,
                UserMapper.PHOTO to user.photo,
                UserMapper.BIO to user.bio
            )
        ).fetchData()
    }

    override suspend fun uploadUserImage(userId: String, imageUri: Uri): Status<Uri> {
        val imageReference = storageReference
            .child("${CollectionConstants.IMAGES_USERS_STORAGE_PATH}/$userId/${imageUri.lastPathSegment}")
        val uploadImageTask = imageReference.putFile(imageUri)

        lateinit var result: Status<Uri>
        uploadImageTask.continueWithTask { task ->
            if (task.isSuccessful.not()) {
                result = Status.error(task.exception?.message, null)
            }
            imageReference.downloadUrl
        }.addOnCompleteListener { task ->
            task.result?.let {
                result = Status.success(it)
            } ?: run {
                result = Status.error(task.exception?.message, null)
            }
        }.await()
        return result
    }

    override suspend fun getUserById(id: String): Status<User> {
        return userCollection.document(id).fetchData(UserMapper::mapToUser)
    }
}