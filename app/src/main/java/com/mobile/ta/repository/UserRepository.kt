package com.mobile.ta.repository

import android.net.Uri
import com.mobile.ta.model.user.User
import com.mobile.ta.model.user.course.chapter.assignment.UserAssignmentAnswer
import com.mobile.ta.model.user.course.chapter.assignment.UserSubmittedAssignment
import com.mobile.ta.model.user.feedback.Feedback
import com.mobile.ta.utils.wrapper.status.Status

interface UserRepository {
    suspend fun submitQuestionResult(
        userId: String,
        userAssignmentAnswer: UserAssignmentAnswer,
        courseId: String,
        chapterId: String
    ): Status<Boolean>

    suspend fun updateCorrectAnswerCount(
        userId: String,
        userSubmittedAssignment: UserSubmittedAssignment,
        courseId: String,
        chapterId: String
    ): Status<Boolean>

    suspend fun getSubmittedChapter(
        userId: String,
        courseId: String,
        chapterId: String
    ): Status<UserSubmittedAssignment>

    suspend fun createNewSubmittedAssignment(userId: String, courseId: String, chapterId: String): Status<Boolean>

    suspend fun addUserFeedback(id: String, data: HashMap<String, Any?>): Status<Boolean>

    suspend fun getUser(): Status<User>

    suspend fun getUserFeedbacks(id: String): Status<MutableList<Feedback>>

    suspend fun updateUser(user: User): Status<Boolean>

    suspend fun uploadUserImage(userId: String, imageUri: Uri): Status<Uri>
}