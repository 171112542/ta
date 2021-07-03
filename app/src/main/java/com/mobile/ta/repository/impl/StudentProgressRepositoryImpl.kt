package com.mobile.ta.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.config.CollectionConstants.ASSIGNMENT_COLLECTION
import com.mobile.ta.model.studentProgress.StudentAssignmentResult
import com.mobile.ta.repository.StudentProgressRepository
import com.mobile.ta.utils.exists
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.mapper.StudentProgressMapper
import com.mobile.ta.utils.mapper.StudentProgressMapper.COURSE_ID_FIELD
import com.mobile.ta.utils.mapper.StudentProgressMapper.FINISHED_CHAPTER_ID_FIELD
import com.mobile.ta.utils.mapper.StudentProgressMapper.STUDENT_ID_FIELD
import com.mobile.ta.utils.wrapper.status.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ExperimentalCoroutinesApi
class StudentProgressRepositoryImpl @Inject constructor(database: FirebaseFirestore): StudentProgressRepository {
    private val studentProgressCollection = database.collection(CollectionConstants.STUDENT_PROGRESS_COLLECTION)

    override suspend fun getSubmittedAssignment(
        userId: String,
        courseId: String,
        assignmentId: String
    ): Status<StudentAssignmentResult> {
        val studentProgressId = studentProgressCollection
            .whereEqualTo(COURSE_ID_FIELD, courseId)
            .whereEqualTo(STUDENT_ID_FIELD, userId)
            .get()
            .await()
            .documents[0]
            .id
        return studentProgressCollection
            .document(studentProgressId)
            .collection(ASSIGNMENT_COLLECTION)
            .document(assignmentId)
            .fetchData(StudentProgressMapper::mapToStudentAssignmentResult)
    }

    override suspend fun getIsChapterCompleted(
        userId: String,
        courseId: String,
        chapterId: String
    ): Status<Boolean> {
        val studentProgressSnapshots = studentProgressCollection
            .whereEqualTo(COURSE_ID_FIELD, courseId)
            .whereEqualTo(STUDENT_ID_FIELD, userId)
            .whereArrayContains(FINISHED_CHAPTER_ID_FIELD, chapterId)
            .get()
            .await()
        return if (studentProgressSnapshots.isEmpty) Status.success(false)
        else Status.success(true)
    }

    override suspend fun saveSubmittedAssignment(
        userId: String,
        courseId: String,
        assignmentId: String,
        studentAssignmentResult: StudentAssignmentResult
    ): Status<Boolean> {
        val studentProgressId = studentProgressCollection
            .whereEqualTo(COURSE_ID_FIELD, courseId)
            .whereEqualTo(STUDENT_ID_FIELD, userId)
            .limit(1)
            .get()
            .await()
            .documents[0]
            .id
        return studentProgressCollection
            .document(studentProgressId)
            .collection(ASSIGNMENT_COLLECTION)
            .document(assignmentId)
            .set(studentAssignmentResult)
            .fetchData()
    }
}