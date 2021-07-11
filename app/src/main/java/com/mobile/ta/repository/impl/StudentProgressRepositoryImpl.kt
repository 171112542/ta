package com.mobile.ta.repository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.config.CollectionConstants.ASSIGNMENT_COLLECTION
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.studentProgress.StudentAssignmentResult
import com.mobile.ta.model.studentProgress.StudentProgress
import com.mobile.ta.repository.StudentProgressRepository
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.mapper.ChapterMapper.toHashMap
import com.mobile.ta.utils.mapper.ShortCourseMapper.toHashMap
import com.mobile.ta.utils.mapper.StudentMapper.toHashMap
import com.mobile.ta.utils.mapper.StudentProgressMapper
import com.mobile.ta.utils.mapper.StudentProgressMapper.COURSE_FIELD
import com.mobile.ta.utils.mapper.StudentProgressMapper.COURSE_ID_FIELD
import com.mobile.ta.utils.mapper.StudentProgressMapper.ENROLLED_FIELD
import com.mobile.ta.utils.mapper.StudentProgressMapper.FINISHED_CHAPTER_ID_FIELD
import com.mobile.ta.utils.mapper.StudentProgressMapper.FINISHED_FIELD
import com.mobile.ta.utils.mapper.StudentProgressMapper.LAST_ACCESSED_CHAPTER_FIELD
import com.mobile.ta.utils.mapper.StudentProgressMapper.STUDENT_FIELD
import com.mobile.ta.utils.mapper.StudentProgressMapper.STUDENT_ID_FIELD
import com.mobile.ta.utils.mapper.StudentProgressMapper.TOTAL_CHAPTER_COUNT_FIELD
import com.mobile.ta.utils.wrapper.status.Status
import com.mobile.ta.utils.wrapper.status.StatusType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ExperimentalCoroutinesApi
class StudentProgressRepositoryImpl @Inject constructor(
    database: FirebaseFirestore,
    private val functions: FirebaseFunctions
) : StudentProgressRepository {
    companion object {
        const val ADD_PRACTICE_ID_FUNCTIONS = "studentProgressFunctions-addFinishedPracticeId"
        const val ADD_QUIZ_ID_FUNCTIONS = "studentProgressFunctions-addFinishedQuizId"
    }

    private val studentProgressCollection =
        database.collection(CollectionConstants.STUDENT_PROGRESS_COLLECTION)

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
        val addAssignmentDocumentStatus = studentProgressCollection
            .document(studentProgressId)
            .collection(ASSIGNMENT_COLLECTION)
            .document(assignmentId)
            .set(studentAssignmentResult)
            .fetchData()
        val data = hashMapOf<String, Any>(
            "assignmentId" to assignmentId,
            "studentProgressId" to studentProgressId
        )
        return if (studentAssignmentResult.type == ChapterType.PRACTICE) {
            if (studentAssignmentResult.score < studentAssignmentResult.passingGrade) {
                addAssignmentDocumentStatus
            } else {
                functions
                    .getHttpsCallable(ADD_PRACTICE_ID_FUNCTIONS)
                    .call(data)
                    .continueWith {
                        it.result?.data
                    }.fetchData()
            }
        } else {
            data["score"] = studentAssignmentResult.score
            functions
                .getHttpsCallable(ADD_QUIZ_ID_FUNCTIONS)
                .call(data)
                .continueWith { it.result?.data }
                .fetchData()
        }
    }

    override suspend fun getStudentProgress(
        userId: String,
        courseId: String
    ): Status<StudentProgress> {
        val studentProgresses = studentProgressCollection
            .whereEqualTo(COURSE_ID_FIELD, courseId)
            .whereEqualTo(STUDENT_ID_FIELD, userId)
            .limit(1)
            .fetchData(StudentProgressMapper::mapToStudentProgress)
        if (studentProgresses.status == StatusType.SUCCESS) {
            return Status.success(studentProgresses.data?.firstOrNull())
        }
        return Status.error(studentProgresses.message)
    }

    override suspend fun getStudentProgressByFinished(
        userId: String,
        finished: Boolean
    ): Status<MutableList<StudentProgress>> {
        return studentProgressCollection
            .whereEqualTo(STUDENT_ID_FIELD, userId)
            .whereEqualTo(FINISHED_FIELD, finished)
            .fetchData(StudentProgressMapper::mapToStudentProgress)
    }

    override suspend fun addStudentProgress(
        userId: String,
        courseId: String,
        studentProgress: StudentProgress
    ): Status<Boolean> {
        val studentProgressMap = hashMapOf(
            COURSE_FIELD to studentProgress.course?.toHashMap(),
            ENROLLED_FIELD to true,
            FINISHED_FIELD to false,
            FINISHED_CHAPTER_ID_FIELD to listOf<String>(),
            LAST_ACCESSED_CHAPTER_FIELD to studentProgress.lastAccessedChapter?.toHashMap(),
            STUDENT_FIELD to studentProgress.student?.toHashMap(),
            TOTAL_CHAPTER_COUNT_FIELD to studentProgress.totalChapterCount
        )
        return studentProgressCollection.add(studentProgressMap).fetchData()
    }

    override suspend fun updateLastAccessedChapter(
        userId: String,
        courseId: String,
        lastAccessedChapter: ChapterSummary
    ): Status<Boolean> {
        val studentProgressId = studentProgressCollection
            .whereEqualTo(COURSE_ID_FIELD, courseId)
            .whereEqualTo(STUDENT_ID_FIELD, userId)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.id
        studentProgressId?.let {
            return studentProgressCollection
                .document(it)
                .update(
                    hashMapOf<String, Any>(
                        LAST_ACCESSED_CHAPTER_FIELD to lastAccessedChapter.toHashMap()
                    )
                ).fetchData()
        }
        return Status.error("Not found.")
    }

    override suspend fun updateFinishedChapter(
        userId: String,
        courseId: String,
        chapterId: String,
        currentFinishedChapterIds: List<String>
    ): Status<Boolean> {
        if (currentFinishedChapterIds.indexOf(chapterId) != -1)
            return Status.success(true)
        val finishedChapterIds = mutableListOf<String>()
        finishedChapterIds.addAll(currentFinishedChapterIds)
        finishedChapterIds.add(chapterId)
        val studentProgressId = studentProgressCollection
            .whereEqualTo(COURSE_ID_FIELD, courseId)
            .whereEqualTo(STUDENT_ID_FIELD, userId)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.id
        studentProgressId?.let {
            return studentProgressCollection
                .document(it)
                .update(
                    hashMapOf<String, Any>(
                        FINISHED_CHAPTER_ID_FIELD to finishedChapterIds
                    )
                ).fetchData()
        }
        return Status.error("Not found.")
    }
}