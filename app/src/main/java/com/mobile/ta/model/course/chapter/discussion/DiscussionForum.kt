package com.mobile.ta.model.course.chapter.discussion

import com.google.firebase.firestore.DocumentId
import java.util.*

data class DiscussionForum(

    @DocumentId
    var id: String,

    var name: String,

    var question: String,

    var createdAt: Date?,

    var userId: String,

    var userName: String,

    var status: String,

    var userImage: String,

    var acceptedAnswerId: String? = null
)