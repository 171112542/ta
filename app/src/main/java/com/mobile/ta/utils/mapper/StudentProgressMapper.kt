package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.ta.model.studentProgress.StudentAssignmentResult
import com.mobile.ta.model.studentProgress.StudentProgress

object StudentProgressMapper {
    const val COURSE_ID_FIELD = "course.id"
    const val STUDENT_ID_FIELD = "student.id"
    const val FINISHED_CHAPTER_ID_FIELD = "finishedChapterIds"
    const val COURSE_FIELD = "course"
    const val STUDENT_FIELD = "student"
    const val ENROLLED_FIELD = "enrolled"
    const val FINISHED_FIELD = "finished"
    const val LAST_ACCESSED_CHAPTER_FIELD = "lastAccessedChapter"
    const val TOTAL_CHAPTER_COUNT_FIELD = "totalChapterCount"

    fun mapToStudentAssignmentResult(documentSnapshot: DocumentSnapshot): StudentAssignmentResult {
        return documentSnapshot.toObject(StudentAssignmentResult::class.java) ?: StudentAssignmentResult()
    }

    fun mapToStudentProgress(querySnapshot: QuerySnapshot): MutableList<StudentProgress> {
        return querySnapshot.toObjects(StudentProgress::class.java)
    }
}