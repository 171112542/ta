package com.mobile.ta.data

import com.mobile.ta.model.discussion.DiscussionForum
import com.mobile.ta.utils.now

object DiscussionData {

    private const val dummyTitle = "Theory of Black Hole"
    private const val dummyQuestion =
        "What do you think about Black Hole? I read that Black Hole is ..., then how could the black hole appears?"

    const val USER_ID = "user_id"
    const val USER_NAME = "user_name"

    val discussionForumsData = arrayListOf(getDiscussionForumData(dummyTitle, dummyQuestion))

    private fun getDiscussionForumData(title: String, question: String) =
        DiscussionForum("123", title, question, now(), USER_ID, USER_NAME, "NEW")
}