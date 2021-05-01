package com.mobile.ta.repository

import com.mobile.ta.model.discussion.DiscussionForum
import com.mobile.ta.model.discussion.DiscussionForumAnswer
import com.mobile.ta.model.status.Status

interface DiscussionRepository {

    suspend fun getDiscussionForums(
        courseId: String, chapterId: String
    ): Status<MutableList<DiscussionForum>>

    suspend fun getDiscussionForumById(
        courseId: String, chapterId: String, id: String
    ): Status<DiscussionForum>

    suspend fun getDiscussionForumAnswers(
        courseId: String, chapterId: String, discussionId: String
    ): Status<MutableList<DiscussionForumAnswer>>

    suspend fun addDiscussionForum(
        courseId: String, chapterId: String, data: HashMap<String, Any?>
    ): Status<Boolean>

    suspend fun addDiscussionForumAnswer(
        courseId: String, chapterId: String, discussionId: String, data: HashMap<String, Any>
    ): Status<Boolean>

    suspend fun markAsAcceptedAnswer(
        courseId: String, chapterId: String, discussionId: String, answerId: String
    ): Status<Boolean>
}