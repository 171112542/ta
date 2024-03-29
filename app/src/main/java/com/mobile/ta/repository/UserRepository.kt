package com.mobile.ta.repository

import android.net.Uri
import com.mobile.ta.model.user.User
import com.mobile.ta.model.user.feedback.Feedback
import com.mobile.ta.utils.wrapper.status.Status

interface UserRepository {

    suspend fun addUserFeedback(id: String, data: HashMap<String, Any?>): Status<Boolean>

    suspend fun getUser(): Status<User>

    suspend fun getUserFeedbacks(id: String): Status<MutableList<Feedback>>

    suspend fun updateUser(user: User): Status<Boolean>

    suspend fun uploadUserImage(userId: String, imageUri: Uri): Status<Uri>

    suspend fun getUserById(id: String) : Status<User>
}