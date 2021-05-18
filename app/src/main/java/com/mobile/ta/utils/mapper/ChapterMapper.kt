package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.ChapterSummary

object ChapterMapper {
    const val ID_FIELD = "id"
    const val TYPE_FIELD = "type"
    const val TITLE_FIELD = "title"
    const val ORDER_FIELD = "order"
    fun mapToChapters(querySnapshot: QuerySnapshot): MutableList<Chapter> {
        return querySnapshot.toObjects(Chapter::class.java)
    }

    fun mapToChapter(documentSnapshot: DocumentSnapshot): Chapter {
        return documentSnapshot.toObject(Chapter::class.java) ?: Chapter()
    }

    fun ChapterSummary.toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            ID_FIELD to id,
            TITLE_FIELD to title,
            TYPE_FIELD to type.toString()
        )
    }
}