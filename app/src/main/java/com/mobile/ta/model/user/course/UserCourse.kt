package com.mobile.ta.model.user.course

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType

data class UserCourse(
    @DocumentId
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val enrolled: Boolean,
    var finished: Boolean,
    val lastAccessedChapter: ChapterSummary? = null
) {
    constructor() :
        this(
            "",
            "",
            "",
            null,
            true,
            false
        )
}