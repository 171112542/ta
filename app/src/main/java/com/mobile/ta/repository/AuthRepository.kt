package com.mobile.ta.repository

import android.content.Intent
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.mobile.ta.model.status.Status
import com.mobile.ta.model.user.NewUser
import com.mobile.ta.model.user.TeacherCredential

interface AuthRepository {

    suspend fun authenticateUser(token: String): Status<Boolean>

    suspend fun checkTeacherCredentials(credentials: String): Status<TeacherCredential?>

    suspend fun getImageUrl(userId: String, imageUri: Uri): Status<Uri>

    suspend fun getIsUserRegistered(): Status<Boolean>

    suspend fun getSignedInAccountFromIntent(data: Intent?): Status<GoogleSignInAccount>

    suspend fun getUser(): FirebaseUser?

    suspend fun registerUser(userId: String, user: NewUser): Status<Boolean>

    suspend fun updateUser(userId: String, user: NewUser): Status<Boolean>

    suspend fun uploadImage(userId: String, imageUri: Uri): Status<Boolean>

    suspend fun logOut()
}