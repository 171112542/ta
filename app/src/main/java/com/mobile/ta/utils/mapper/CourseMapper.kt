package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.ta.model.course.Course

object CourseMapper {
    const val CANONICAL_TITLE_FIELD = "canonicalTitle"
    const val TYPE_FIELD = "type"
    const val LEVEL_FIELD = "level"
    const val DESC_FIELD = "description"
    const val IMGURL_FIELD = "imageUrl"
    const val TOTAL_ENROLLED_FIELD = "totalEnrolled"

    fun mapToCourses(querySnapshot: QuerySnapshot): MutableList<Course> {
        return querySnapshot.toObjects(Course::class.java)
    }

    fun mapToCourse(documentSnapshot: DocumentSnapshot): Course {
        return documentSnapshot.toObject(Course::class.java) ?: Course()
    }

    fun Course.toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            TOTAL_ENROLLED_FIELD to totalEnrolled
        )
    }
}