package com.mobile.ta.utils.mapper

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.user.course.UserCourse
import com.mobile.ta.utils.mapper.ChapterMapper.toHashMap

object UserCourseMapper {
    const val TITLE_FIELD = "title"
    const val DESCRIPTION_FIELD = "description"
    const val IMAGE_URL_FIELD = "imageUrl"
    const val FINISHED_FIELD = "finished"
    const val LAST_ACCESSED_CHAPTER_FIELD = "lastAccessedChapter"
    const val ENROLLED_FIELD = "enrolled"
    fun mapToUserCourses(querySnapshot: QuerySnapshot): MutableList<UserCourse> {
        return querySnapshot.toObjects(UserCourse::class.java)
    }

    fun mapToUserCourse(documentSnapshot: DocumentSnapshot): UserCourse? {
        return documentSnapshot.toObject(UserCourse::class.java)
    }

    fun UserCourse.toHashMap(lastAccessedChapter: ChapterSummary? = null): HashMap<String, Any?> {
        val userCourse = hashMapOf<String, Any?>(
            TITLE_FIELD to title,
            DESCRIPTION_FIELD to description,
            IMAGE_URL_FIELD to imageUrl,
            FINISHED_FIELD to finished,
            ENROLLED_FIELD to enrolled
        )
        lastAccessedChapter?.let {
            userCourse[LAST_ACCESSED_CHAPTER_FIELD] = it.toHashMap()
        }
        return userCourse
    }
}