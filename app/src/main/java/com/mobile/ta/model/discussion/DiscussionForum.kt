package com.mobile.ta.model.discussion

import java.util.*

data class DiscussionForum(
    var id: String,
    var name: String,
    var question: String,
    var createdAt: Date,
    var userId: String,
    var userName: String,
    var status: String,
    var answer: MutableMap<String, DiscussionForumAnswer> = mutableMapOf(),
    var acceptedAnswerId: String? = null
)