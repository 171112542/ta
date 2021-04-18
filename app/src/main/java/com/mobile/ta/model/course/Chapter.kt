package com.mobile.ta.model.course

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

enum class Type {
    CHAPTER, PRACTICE
}

data class Chapter(
    val id: String? = null,
    val name: String? = null,
    val type: String? = null,
    val content: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val nextChapterId: String? = null,
    val previousChapterId: String? = null,
    val nextChapterType: String? = null,
    val previousChapterType: String? = null
)