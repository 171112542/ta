package com.mobile.ta.model.course.chapter

import kotlinx.serialization.Serializable

@Serializable
data class ChapterSummary(
    val id: String? = null,
    val title: String? = null,
    val type: ChapterType? = null
)