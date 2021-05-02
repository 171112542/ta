package com.mobile.ta.repository

import com.mobile.ta.model.course.chapter.assignment.UserAssignmentAnswer
import com.mobile.ta.model.course.chapter.assignment.UserSubmittedAssignment
import com.mobile.ta.model.status.Status
import android.net.Uri
import com.mobile.ta.model.user.User

interface UserRepository {
    suspend fun submitQuestionResult(userAssignmentAnswer: UserAssignmentAnswer, courseId: String, chapterId: String): Status<Boolean>

    suspend fun updateCorrectAnswerCount(userSubmittedAssignment: UserSubmittedAssignment, courseId: String, chapterId: String): Status<Boolean>

    suspend fun resetSubmittedChapter(courseId: String, chapterId: String): Status<Boolean>

    suspend fun getSubmittedChapter(courseId: String, chapterId: String): Status<UserSubmittedAssignment>

    suspend fun getIfSubmittedBefore(courseId: String, chapterId: String): Status<Boolean>

    suspend fun createNewSubmittedAssignment(courseId: String, chapterId: String): Status<Boolean>

    suspend fun getUserById(id: String): Status<User>

    suspend fun getUserImageUrl(userId: String, imageUri: Uri): Status<Uri>

    suspend fun updateUser(user: User): Status<Boolean>

    suspend fun uploadUserImage(userId: String, imageUri: Uri): Status<Boolean>
}