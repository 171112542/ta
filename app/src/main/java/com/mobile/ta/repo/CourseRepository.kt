package com.mobile.ta.repo

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CourseRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val chapters = db.collection(CHAPTER_COLLECTION_PATH)

    suspend fun getNthChapter(index: Int) =
        chapters.whereEqualTo(CHAPTER_ORDER_FIELD, index)
            .get()
            .await()

    suspend fun getChapterById(chapterId: String) =
        chapters.document(chapterId)
            .get()
            .await()

    suspend fun getQuestions(chapterId: String) =
        chapters.document(chapterId)
            .collection(QUESTION_COLLECTION_PATH)
            .orderBy(QUESTION_ORDER_FIELD)
            .get()
            .await()

    companion object {
        private const val COURSE_COLLECTION_PATH = "course"
        private const val CHAPTER_COLLECTION_PATH = "chapter"
        private const val QUESTION_COLLECTION_PATH = "question"
        const val CHAPTER_ORDER_FIELD = "order"
        const val CHAPTER_NEXT_CHAPTER_ID_FIELD = "nextChapterId"
        const val CHAPTER_TYPE_FIELD = "type"
        const val CHAPTER_TYPE_FIELD_PRACTICE = "practice"
        const val CHAPTER_TYPE_FIELD_QUIZ = "quiz"
        const val CHAPTER_TYPE_FIELD_CONTENT = "content"
        private const val QUESTION_ORDER_FIELD = "order"
    }
}