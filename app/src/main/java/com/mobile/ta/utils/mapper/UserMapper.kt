package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.ta.model.user.TeacherCredential
import com.mobile.ta.model.user.User
import com.mobile.ta.model.user.UserRoleEnum
import com.mobile.ta.model.user.feedback.Feedback

object UserMapper {

    const val USER_ID = "user_id"
    const val EMAIL = "email"
    const val NAME = "name"
    const val PHOTO = "photo"
    const val BIRTH_DATE = "birthDate"
    const val BIO = "bio"
    const val DEACTIVATED = "deactivated"
    const val ROLE = "role"
    const val FEEDBACK_TYPE = "feedbackType"
    const val DESCRIPTION = "description"
    const val CREATED_AT = "createdAt"
    const val TOTAL_COURSE_CREATED = "totalCourseCreated"

    fun mapToTeacherCredential(snapshot: DocumentSnapshot): TeacherCredential? {
        val userId: String = snapshot.getString(USER_ID).orEmpty()
        val email: String? = snapshot.getString(EMAIL)

        return if (snapshot.exists()) {
            TeacherCredential(userId, email)
        } else {
            null
        }
    }

    fun mapToUser(snapshot: DocumentSnapshot): User {
        val id: String = snapshot.id
        val name: String = snapshot.getString(NAME).orEmpty()
        val email: String = snapshot.getString(EMAIL).orEmpty()
        val photo: String? = snapshot.getString(PHOTO)
        val birthDate: Long? = snapshot.getLong(BIRTH_DATE)
        val role: UserRoleEnum? = snapshot.getString(ROLE)?.let {
            UserRoleEnum.valueOf(it)
        }
        val bio: String? = snapshot.getString(BIO)
        val deactivated: Boolean = snapshot.getBoolean(DEACTIVATED) ?: false
        val totalCourseCreated: Int = snapshot.getLong(TOTAL_COURSE_CREATED)?.toInt() ?: 0

        return User(id, name, email, photo, birthDate, role, bio, deactivated, totalCourseCreated)
    }

    fun mapToUserFeedbacks(snapshot: QuerySnapshot): MutableList<Feedback> {
        return snapshot.toObjects(Feedback::class.java)
    }
}