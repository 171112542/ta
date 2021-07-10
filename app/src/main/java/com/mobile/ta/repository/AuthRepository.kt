package com.mobile.ta.repository

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.mobile.ta.model.user.User
import com.mobile.ta.utils.wrapper.status.Status

interface AuthRepository {

    suspend fun authenticateUser(token: String): Status<Boolean>

    suspend fun checkTeacherCredentials(credentials: String): Status<Boolean>

    suspend fun getIsUserRegistered(): Status<Boolean>

    suspend fun getSignedInAccountFromIntent(data: Intent?): Status<GoogleSignInAccount>

    fun getUser(): FirebaseUser?

    suspend fun registerUser(user: User): Status<Boolean>

    suspend fun logOut()
}