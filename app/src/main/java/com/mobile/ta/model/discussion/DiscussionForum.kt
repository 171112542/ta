package com.mobile.ta.model.discussion

import java.util.Date

data class DiscussionForum(
    val id: String,
    val name: String,
    val question: String,
    val createdAt: Date,
    val userId: String,
    val userName: String,
    val status: String,
    val answer: List<Map<String, DiscussionForumAnswer>> = listOf(),
    val acceptedAnswerId: String? = null
)