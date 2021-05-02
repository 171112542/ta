package com.mobile.ta.repository.impl

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.model.status.Status
import com.mobile.ta.model.user.User
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.fetchDataWithResult
import com.mobile.ta.utils.mapper.UserMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class UserRepositoryImpl @Inject constructor(
    database: FirebaseFirestore,
    private val storage: FirebaseStorage
) : UserRepository {

    private val userCollection by lazy {
        database.collection(CollectionConstants.USER_COLLECTION)
    }

    private val storageReference by lazy {
        storage.reference
    }

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