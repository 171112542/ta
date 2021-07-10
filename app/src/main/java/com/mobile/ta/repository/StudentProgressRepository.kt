package com.mobile.ta.repository

import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.studentProgress.StudentAssignmentResult
import com.mobile.ta.model.studentProgress.StudentProgress
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

    suspend fun getStudentProgress(
        userId: String,
        courseId: String
    ): Status<StudentProgress>

    suspend fun getStudentProgressByFinished(
        userId: String,
        finished: Boolean
    ): Status<MutableList<StudentProgress>>

    suspend fun addStudentProgress(
        userId: String,
        courseId: String,
        studentProgress: StudentProgress
    ): Status<Boolean>

    suspend fun updateLastAccessedChapter(
        userId: String,
        courseId: String,
        lastAccessedChapter: ChapterSummary
    ): Status<Boolean>

    suspend fun updateFinishedChapter(
        userId: String,
        courseId: String,
        chapterId: String,
        currentFinishedChapterIds: List<String>
    ): Status<Boolean>
}