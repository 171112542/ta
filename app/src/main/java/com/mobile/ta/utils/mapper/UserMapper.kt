package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.mobile.ta.model.user.TeacherCredential

object UserMapper {

    const val USER_ID = "user_id"
    const val EMAIL = "email"
    const val NAME = "name"
    const val PHOTO = "photo"
    const val BIRTH_DATE = "birthDate"

    fun mapToTeacherCredential(snapshot: DocumentSnapshot): TeacherCredential? {
        val userId: String = snapshot.getString(USER_ID).orEmpty()
        val email: String? = snapshot.getString(EMAIL)

        return if (snapshot.exists()) {
            TeacherCredential(userId, email)
        } else {
            null
        }
    }
}