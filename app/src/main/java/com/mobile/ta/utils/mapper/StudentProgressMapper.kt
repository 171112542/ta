package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.mobile.ta.model.studentProgress.StudentAssignmentResult

object StudentProgressMapper {
    const val COURSE_ID_FIELD = "course.id"
    const val STUDENT_ID_FIELD = "student.id"
    const val FINISHED_CHAPTER_ID_FIELD = "finishedChapterIds"

    fun mapToStudentAssignmentResult(documentSnapshot: DocumentSnapshot): StudentAssignmentResult {
        return documentSnapshot.toObject(StudentAssignmentResult::class.java) ?: StudentAssignmentResult()
    }
}