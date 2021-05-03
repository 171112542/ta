package com.mobile.ta.model.course.chapter

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.mobile.ta.model.course.chapter.content.Sketchfab
import com.mobile.ta.utils.serializer.TimestampSerializer
import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    @DocumentId
    val id: String,
    val title: String,
    val type: ChapterType,
    val content: String,
    @Serializable(with = TimestampSerializer::class)
    val createdAt: Timestamp,
    @Serializable(with = TimestampSerializer::class)
    val updatedAt: Timestamp,
    val nextChapter: ChapterSummary? = null,
    val previousChapter: ChapterSummary? = null,
    val nextChapterType: ChapterType,
    val previousChapterType: ChapterType,
    val totalDiscussion: Int,
    val sketchfab: Sketchfab? = null,
    val order: Int,
    val typeOrder: Int
) {
    constructor() :
        this(
            "",
            "",
            ChapterType.CONTENT,
            "",
            Timestamp.now(),
            Timestamp.now(),
            null,
            null,
            ChapterType.CONTENT,
            ChapterType.CONTENT,
            0,
            null,
            0,
            0
        )
}
