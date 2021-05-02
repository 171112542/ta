package com.mobile.ta.model.course

import com.google.firebase.firestore.DocumentId
import com.mobile.ta.model.LevelTag
import com.mobile.ta.model.TypeTag
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.discussion.DiscussionForum

data class Course(
    @DocumentId
    val id: String,
    val title: String,
    val canonicalTitle: String,
    val description: String,
    val imageUrl: String,
    val level: LevelTag,
    val type: TypeTag,
    val chapterSummaryList: List<ChapterSummary>,
    val discussions: List<DiscussionForum>
) {
    constructor(): this("", "", "", "", "", LevelTag.JUNIOR_ONE, TypeTag.CHEMISTRY, listOf(), listOf())
}
