package com.mobile.ta.model.course.chapter

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Chapter(
    @DocumentId
    val id: String,
    val title: String,
    val type: ChapterType,
    val content: String,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    val nextChapterId: String,
    val previousChapterId: String,
    val nextChapterType: ChapterType,
    val previousChapterType: ChapterType
) {
    constructor():
        this(
            "",
            "",
            ChapterType.CONTENT,
            "",
            Timestamp.now(),
            Timestamp.now(),
            "",
            "",
            ChapterType.CONTENT,
            ChapterType.CONTENT
        )
}