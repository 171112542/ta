package com.mobile.ta.repository.impl

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.mobile.ta.model.Status
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.Course
import kotlinx.coroutines.tasks.await

class CourseRepository {
    private val db = Firebase.firestore
    private val courseCollection = db.collection("steven")
        .document("1").collection("course")

    suspend fun getCourse(courseId: String): Status<Course> {
        val result = courseCollection.document(courseId).get().await().toObject<Course>()
        if (result != null)
            return Status.success(result)
        else
            return Status.error("Failed to fetch", null)
    }

    suspend fun getChapter(courseId: String, chapterId: String): Status<Chapter> {
        val result =
            courseCollection.document(courseId).collection("chapter").document(chapterId).get()
                .await().toObject<Chapter>()
        if (result != null)
            return Status.success(result)
        else
            return Status.error("Failed to fetch", null)
    }
}