package com.mobile.ta.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.ta.model.CourseQuestionAnswer
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val users = db.collection(USER_COLLECTION_PATH)

    // TODO: Change document path to logged in user ID
    suspend fun submitQuestionResult(courseQuestionAnswer: CourseQuestionAnswer, courseId: String, chapterId: String) =
        users.document("l1CLTummIoarBY3Wb3FY")
            .collection(COURSE_COLLECTION_PATH).document(courseId)
            .collection(CHAPTER_COLLECTION_PATH).document(chapterId)
            .collection(QUESTION_COLLECTION_PATH).document(courseQuestionAnswer.id)
            .set(courseQuestionAnswer)
            .await()

    suspend fun updateCorrectAnswerCount(correctAnswerCount: Int, totalAnswerCount: Int, courseId: String, chapterId: String) =
        users.document("l1CLTummIoarBY3Wb3FY")
            .collection(COURSE_COLLECTION_PATH).document(courseId)
            .collection(CHAPTER_COLLECTION_PATH).document(chapterId)
            .update(
                mapOf(
                    CORRECT_ANSWER_COUNT_FIELD to correctAnswerCount,
                    TOTAL_ANSWER_COUNT_FIELD to totalAnswerCount
                )
            )
            .await()

    suspend fun resetSubmittedChapter(courseId: String, chapterId: String) =
        users.document("l1CLTummIoarBY3Wb3FY")
            .collection(COURSE_COLLECTION_PATH).document(courseId)
            .collection(CHAPTER_COLLECTION_PATH).document(chapterId)
            .delete()
            .await()

    suspend fun getSubmittedChapter(courseId: String, chapterId: String) =
        users.document("l1CLTummIoarBY3Wb3FY")
            .collection(COURSE_COLLECTION_PATH).document(courseId)
            .collection(CHAPTER_COLLECTION_PATH).document(chapterId)
            .get()
            .await()

    suspend fun getIfSubmittedBefore(courseId: String, chapterId: String) =
        users.document("l1CLTummIoarBY3Wb3FY")
            .collection(COURSE_COLLECTION_PATH).document(courseId)
            .collection(CHAPTER_COLLECTION_PATH).document(chapterId)
            .get()
            .await()
            .exists()

    suspend fun createNewSubmittedChapter(type: String, courseId: String, chapterId: String) =
        users.document("l1CLTummIoarBY3Wb3FY")
            .collection(COURSE_COLLECTION_PATH).document(courseId)
            .collection(CHAPTER_COLLECTION_PATH).document(chapterId)
            .set(
                hashMapOf(
                    CHAPTER_TYPE_FIELD to type,
                    CORRECT_ANSWER_COUNT_FIELD to -1
                )
            )
            .await()

    companion object {
        private const val USER_COLLECTION_PATH = "users"
        private const val COURSE_COLLECTION_PATH = "course"
        private const val CHAPTER_COLLECTION_PATH = "chapter"
        private const val QUESTION_COLLECTION_PATH = "question"
        private const val CHAPTER_TYPE_FIELD = "type"
        const val CHAPTER_TYPE_FIELD_PRACTICE = "practice"
        const val CHAPTER_TYPE_FIELD_QUIZ = "quiz"
        const val CORRECT_ANSWER_COUNT_FIELD = "correctAnswerCount"
        const val TOTAL_ANSWER_COUNT_FIELD = "totalAnswerCount"
    }
}