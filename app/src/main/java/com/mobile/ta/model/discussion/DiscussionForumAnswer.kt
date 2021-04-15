package com.mobile.ta.model.discussion

import java.util.Date

data class DiscussionForumAnswer(
    var id: String,
    var answer: String,
    var createdAt: Date?,
    var userId: String,
    var userName: String,
    var isAccepted: Boolean = false
)