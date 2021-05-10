package com.mobile.ta.model.course.chapter.discussion

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class DiscussionForumAnswer(

    @DocumentId
    var id: String,

    var answer: String,

    var createdAt: Date?,

    var userId: String,

    var userName: String,

    var userImage: String,

    var isAccepted: Boolean = false
)
