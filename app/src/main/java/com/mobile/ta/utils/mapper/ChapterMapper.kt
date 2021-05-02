package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.ta.model.course.chapter.Chapter

object ChapterMapper {
    const val ORDER_FIELD = "order"

    fun mapToChapters(querySnapshot: QuerySnapshot): MutableList<Chapter> {
        return querySnapshot.toObjects(Chapter::class.java)
    }

    fun mapToChapter(documentSnapshot: DocumentSnapshot): Chapter {
        return documentSnapshot.toObject(Chapter::class.java) ?: Chapter()
    }
}