package com.mobile.ta.repo

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobile.ta.model.CourseQuestionAnswer
import kotlinx.coroutines.tasks.await

class CourseRepository {
    private val db = Firebase.firestore
    private val chapters = db.collection("chapter")
    private val users = db.collection("users")

    fun getNthChapter(index: Int) = chapters
        .whereEqualTo("order", index)
        .get()

    fun getQuestions(chapterId: String) = chapters
        .document(chapterId)
        .collection("question")
        .orderBy("order")
        .get()

    fun submitQuestionResult(courseQuestionAnswer: CourseQuestionAnswer, courseId: String, chapterId: String) = users
        .document("l1CLTummIoarBY3Wb3FY")
        .collection("course")
        .document(courseId)
        .collection("chapter")
        .document(chapterId)
        .collection("question")
        .document(courseQuestionAnswer.id)
        .set(courseQuestionAnswer)

    fun updateCorrectAnswerCount(correctAnswerCount: Int, totalAnswerCount: Int, courseId: String, chapterId: String) = users
        .document("l1CLTummIoarBY3Wb3FY")
        .collection("course")
        .document(courseId)
        .collection("chapter")
        .document(chapterId)
        .update(mapOf(
            "correctAnswerCount" to correctAnswerCount,
            "totalAnswerCount" to totalAnswerCount
        ))

    fun resetSubmittedChapter(courseId: String, chapterId: String) = users
        .document("l1CLTummIoarBY3Wb3FY")
        .collection("course")
        .document(courseId)
        .collection("chapter")
        .document(chapterId)
        .delete()

    fun getSubmittedChapter(courseId: String, chapterId: String) = users
        .document("l1CLTummIoarBY3Wb3FY")
        .collection("course")
        .document(courseId)
        .collection("chapter")
        .document(chapterId)
        .get()

    suspend fun getIfSubmittedBefore(courseId: String, chapterId: String) = users
        .document("l1CLTummIoarBY3Wb3FY")
        .collection("course")
        .document(courseId)
        .collection("chapter")
        .document(chapterId)
        .get()
        .await()
        .exists()

    fun createNewSubmittedChapter(courseId: String, chapterId: String) = users
        .document("l1CLTummIoarBY3Wb3FY")
        .collection("course")
        .document(courseId)
        .collection("chapter")
        .document(chapterId)
        .set(hashMapOf(
            "correctAnswerCount" to -1
        ))
}