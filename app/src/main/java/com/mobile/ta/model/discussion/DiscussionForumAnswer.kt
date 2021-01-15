package com.mobile.ta.model.discussion

import java.util.Date

data class DiscussionForumAnswer(
    val id: String,
    val answer: String,
    val createdAt: Date,
    val userId: String,
    val userName: String
)