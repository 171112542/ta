package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.QuerySnapshot
import com.mobile.ta.model.user.course.chapter.UserChapter

object UserChapterMapper {
    const val TITLE_FIELD = "title"
    fun mapToUserChapters(querySnapshot: QuerySnapshot): MutableList<UserChapter> {
        return querySnapshot.toObjects(UserChapter::class.java)
    }

    fun UserChapter.toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            TITLE_FIELD to title
        )
    }
}