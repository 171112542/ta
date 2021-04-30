package com.mobile.ta.repository.impl

import android.content.Intent
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.model.status.Status
import com.mobile.ta.model.user.NewUser
import com.mobile.ta.model.user.TeacherCredential
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.fetchDataWithResult
import com.mobile.ta.utils.mapper.UserMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    database: FirebaseFirestore,
    private val storage: FirebaseStorage
) : AuthRepository {

    private val userCollection by lazy {
        database.collection(CollectionConstants.USER_COLLECTION)
    }

    private val tokenCollection by lazy {
        database.collection(CollectionConstants.TOKEN_COLLECTION)
    }

    private val storageReference by lazy {
        storage.reference
    }

    override suspend fun authenticateUser(token: String): Status<Boolean> {
        val credentials = GoogleAuthProvider.getCredential(token, null)
        return auth.signInWithCredential(credentials).fetchData()
    }

    override suspend fun checkTeacherCredentials(credentials: String): Status<TeacherCredential?> {
        return tokenCollection.document(credentials).fetchData(UserMapper::mapToTeacherCredential)
    }

    override suspend fun getImageUrl(userId: String, imageUri: Uri): Status<Uri> {
        return storageReference
            .child("${CollectionConstants.IMAGES_USERS_STORAGE_PATH}/$userId/${imageUri.lastPathSegment}")
            .downloadUrl.fetchDataWithResult()
    }

    override suspend fun getIsUserRegistered(): Status<Boolean> {
        return getUser()?.uid?.let { userId ->
            userCollection.document(userId).fetchData {
                it.exists()
            }
        } ?: Status.success(false)
    }

    override suspend fun getSignedInAccountFromIntent(data: Intent?): Status<GoogleSignInAccount> {
        return GoogleSignIn.getSignedInAccountFromIntent(data).fetchDataWithResult()
    }

    override suspend fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun registerUser(user: NewUser): Status<Boolean> {
        return userCollection.document(user.id).set(user).fetchData()
    }

    override suspend fun updateUser(user: NewUser): Status<Boolean> {
        return userCollection.document(user.id).update(
            mapOf(
                UserMapper.NAME to user.name,
                UserMapper.BIRTH_DATE to user.birthDate,
                UserMapper.PHOTO to user.photo
            )
        ).fetchData()
    }

    override suspend fun uploadImage(userId: String, imageUri: Uri): Status<Boolean> {
        return storageReference
            .child("${CollectionConstants.IMAGES_USERS_STORAGE_PATH}/$userId/${imageUri.lastPathSegment}")
            .putFile(imageUri).fetchData()
    }

    override suspend fun logOut() {
        auth.signOut()
    }
}