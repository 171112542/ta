package com.mobile.ta.model.course.chapter

import kotlinx.serialization.Serializable

@Serializable
data class ChapterSummary(
    val id: String,
    val title: String,
    val type: ChapterType
) {
    constructor() : this("", "", ChapterType.CONTENT)
}