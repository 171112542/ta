package com.mobile.ta.model.course.information

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.mobile.ta.model.course.content.Sketchfab
import com.mobile.ta.utils.serializer.TimestampSerializer
import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    @DocumentId
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val type: ChapterType? = null,
    val content: String? = null,
    @Serializable(with = TimestampSerializer::class)
    val createdAt: Timestamp? = null,
    @Serializable(with = TimestampSerializer::class)
    val updatedAt: Timestamp? = null,
    val nextChapter: ChapterSummary? = null,
    val previousChapter: ChapterSummary? = null,
    val sketchfab: Sketchfab? = null,
    val totalDiscussion: Int? = null,
    val order: Int? = null,
    val typeOrder: Int? = null
)
