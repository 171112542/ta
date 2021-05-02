package com.mobile.ta.repository

import android.content.Intent
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.mobile.ta.model.user.NewUser
import com.mobile.ta.model.user.TeacherCredential
import com.mobile.ta.utils.wrapper.status.Status

interface AuthRepository {

    suspend fun authenticateUser(token: String): Status<Boolean>

    suspend fun checkTeacherCredentials(credentials: String): Status<TeacherCredential?>

    suspend fun getImageUrl(userId: String, imageUri: Uri): Status<Uri>

    suspend fun getIsUserRegistered(): Status<Boolean>

    suspend fun getSignedInAccountFromIntent(data: Intent?): Status<GoogleSignInAccount>

    suspend fun getUser(): FirebaseUser?

    suspend fun registerUser(user: NewUser): Status<Boolean>

    suspend fun updateUser(user: NewUser): Status<Boolean>

    suspend fun uploadImage(userId: String, imageUri: Uri): Status<Boolean>

    suspend fun logOut()
}