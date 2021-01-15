package com.mobile.ta.data

import com.mobile.ta.model.discussion.DiscussionForum
import com.mobile.ta.utils.now
import java.util.Date

object DiscussionData {

    private const val dummyTitle = "Theory of Black Hole"
    private const val dummyQuestion =
        "What do you think about Black Hole? I read that Black Hole is ..., then how could the black hole appears?"

    val discussionForumsData = arrayListOf<DiscussionForum>(getDiscussionForumData(dummyTitle, dummyQuestion))

    fun getDiscussionForumData(title: String, question: String) =
        DiscussionForum("123", title, question, Date().now(), "NEW", "user_id", "username")
}