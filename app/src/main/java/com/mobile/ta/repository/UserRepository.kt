package com.mobile.ta.repository

import android.net.Uri
import com.mobile.ta.model.status.Status
import com.mobile.ta.model.user.User

interface UserRepository {

    suspend fun getUserById(id: String): Status<User>

    suspend fun getUserImageUrl(userId: String, imageUri: Uri): Status<Uri>

    suspend fun updateUser(user: User): Status<Boolean>

    suspend fun uploadUserImage(userId: String, imageUri: Uri): Status<Boolean>
}