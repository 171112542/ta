package com.mobile.ta.repository.impl

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.mobile.ta.config.CollectionConstants.CHAPTER_COLLECTION
import com.mobile.ta.config.CollectionConstants.COURSE_COLLECTION
import com.mobile.ta.model.course.information.Chapter
import com.mobile.ta.model.course.information.Course
import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.utils.wrapper.status.Status
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore
) : CourseRepository {
    private val coursesCollection =
        database.collection(COURSE_COLLECTION)

    private val chaptersCollection =
        database.collection(CHAPTER_COLLECTION)

    override suspend fun getCourse(courseId: String): Status<Course> {
        val result = coursesCollection.document(courseId).get().await()
            .toObject<Course>()
        return if (result != null)
            Status.success(result)
        else
            Status.error("Failed to fetch", null)
    }

    override suspend fun getChapter(courseId: String, chapterId: String): Status<Chapter> {
        val result =
            coursesCollection.document(courseId).collection(CHAPTER_COLLECTION)
                .document(chapterId).get()
                .await().toObject<Chapter>()
        return if (result != null)
            Status.success(result)
        else
            Status.error("Failed to fetch", null)
    }

    override suspend fun getChapters(courseId: String): Status<List<Chapter>> {
        val result =
            coursesCollection.document(courseId).collection(CHAPTER_COLLECTION)
                .get()
                .await().toObjects(Chapter::class.java)
        return Status.success(result)
    }

    override suspend fun getNthChapter(index: Int): QuerySnapshot =
        chaptersCollection.whereEqualTo(CHAPTER_ORDER_FIELD, index)
            .get()
            .await()

    override suspend fun getChapterById(chapterId: String): DocumentSnapshot =
        chaptersCollection.document(chapterId)
            .get()
            .await()

    override suspend fun getQuestions(chapterId: String): QuerySnapshot =
        chaptersCollection.document(chapterId)
            .collection(QUESTION_COLLECTION_PATH)
            .orderBy(QUESTION_ORDER_FIELD)
            .get()
            .await()

    companion object {
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