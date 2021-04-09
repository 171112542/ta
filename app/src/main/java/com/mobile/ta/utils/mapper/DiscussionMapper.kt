package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.mobile.ta.model.discussion.DiscussionForum
import java.util.Date

object DiscussionMapper {

    private const val FORUM_ID = "id"
    private const val FORUM_NAME = "name"
    private const val FORUM_QUESTION = "question"
    private const val FORUM_CREATED_AT = "createdAt"
    private const val FORUM_USER_ID = "userId"
    private const val FORUM_USER_NAME = "userName"
    private const val FORUM_STATUS = "status"
    private const val FORUM_ANSWER = "answer"
    private const val FORUM_ACCEPTED_ANSWER_ID = "acceptedAnswerId"

    fun mapToDiscussionForums(snapshots: MutableList<DocumentSnapshot>): MutableList<DiscussionForum> {
        return DataMapper.mapToLists(snapshots, ::mapToDiscussionForum)
    }

    fun mapToDiscussionForum(snapshot: DocumentSnapshot): DiscussionForum {
        var id: String = snapshot.getString(FORUM_ID).orEmpty()
        var name: String = snapshot.getString(FORUM_NAME).orEmpty()
        var question: String = snapshot.getString(FORUM_QUESTION).orEmpty()
        var createdAt: Date? = snapshot.getDate(FORUM_CREATED_AT)
        var userId: String = snapshot.getString(FORUM_USER_ID).orEmpty()
        var userName: String = snapshot.getString(FORUM_USER_NAME).orEmpty()
        var status: String = snapshot.getString(FORUM_STATUS).orEmpty()
        var acceptedAnswerId: String? = snapshot.getString(FORUM_ACCEPTED_ANSWER_ID).orEmpty()

        return DiscussionForum(
            id = id,
            name = name,
            question = question,
            createdAt = createdAt,
            userId = userId,
            userName = userName,
            status = status,
            acceptedAnswerId = acceptedAnswerId
        )
    }
}