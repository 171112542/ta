package com.mobile.ta.model.course.discussion

import java.util.Date

data class DiscussionForum(

    var id: String,

    var name: String,

    var question: String,

    var createdAt: Date?,

    var userId: String,

    var userName: String,

    var status: String,

    var acceptedAnswerId: String? = null
)