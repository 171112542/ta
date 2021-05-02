package com.mobile.ta.repository

import com.mobile.ta.model.course.discussion.DiscussionForum
import com.mobile.ta.model.course.discussion.DiscussionForumAnswer
import com.mobile.ta.utils.wrapper.status.Status

interface DiscussionRepository {

    suspend fun getDiscussionForums(): Status<MutableList<DiscussionForum>>

    suspend fun getDiscussionForumById(id: String): Status<DiscussionForum>

    suspend fun getDiscussionForumAnswers(
        discussionId: String
    ): Status<MutableList<DiscussionForumAnswer>>

    suspend fun addDiscussionForum(data: HashMap<String, Any?>): Status<Boolean>

    suspend fun addDiscussionForumAnswer(
        discussionId: String,
        data: HashMap<String, Any>
    ): Status<Boolean>

    suspend fun markAsAcceptedAnswer(discussionId: String, answerId: String): Status<Boolean>
}