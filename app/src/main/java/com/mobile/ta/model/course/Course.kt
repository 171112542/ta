package com.mobile.ta.model.course

import com.google.firebase.firestore.DocumentId
import com.mobile.ta.model.discussion.DiscussionForum

data class Course(
    @DocumentId
    val id: String? = null,
    val chapters: List<MChapter>? = null,
    val name: String? = null,
    val discussions: List<DiscussionForum>? = null
)

data class MChapter(
    val id: String? = null,
    val name: String? = null,
    val type: String? = null
)