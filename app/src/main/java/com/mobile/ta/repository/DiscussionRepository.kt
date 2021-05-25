package com.mobile.ta.repository

import com.google.firebase.firestore.Query
import com.mobile.ta.model.course.chapter.discussion.DiscussionForum
import com.mobile.ta.utils.wrapper.status.Status

interface DiscussionRepository {

    suspend fun getDiscussionForums(
        courseId: String, chapterId: String
    ): Status<MutableList<DiscussionForum>>

    suspend fun getDiscussionForumById(
        courseId: String, chapterId: String, id: String
    ): Status<DiscussionForum>

    suspend fun getDiscussionForumAnswers(
        courseId: String, chapterId: String, discussionId: String): Query

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