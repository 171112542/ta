package com.mobile.ta.repository

import com.mobile.ta.model.studentProgress.StudentAssignmentResult
import com.mobile.ta.utils.wrapper.status.Status

interface StudentProgressRepository {
    suspend fun getSubmittedAssignment(
        userId: String,
        courseId: String,
        assignmentId: String
    ): Status<StudentAssignmentResult>

    suspend fun getIsChapterCompleted(
        userId: String,
        courseId: String,
        chapterId: String
    ): Status<Boolean>

    suspend fun saveSubmittedAssignment(
        userId: String,
        courseId: String,
        assignmentId: String,
        studentAssignmentResult: StudentAssignmentResult
    ): Status<Boolean>
}