package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.mobile.ta.model.discussion.DiscussionForum
import com.mobile.ta.model.discussion.DiscussionForumAnswer
import java.util.Date

object DiscussionMapper {

    const val FORUM_NAME = "name"
    const val FORUM_QUESTION = "question"
    const val FORUM_CREATED_AT = "createdAt"
    const val FORUM_USER_ID = "userId"
    const val FORUM_USER_NAME = "userName"
    const val FORUM_STATUS = "status"
    const val FORUM_ANSWER = "answer"
    const val FORUM_ACCEPTED_ANSWER_ID = "acceptedAnswerId"

    fun mapToDiscussionForums(snapshots: MutableList<DocumentSnapshot>): MutableList<DiscussionForum> {
        return DataMapper.mapToLists(snapshots, ::mapToDiscussionForum)
    }

    fun mapToDiscussionForum(snapshot: DocumentSnapshot): DiscussionForum {
        val id: String = snapshot.id
        val name: String = snapshot.getString(FORUM_NAME).orEmpty()
        val question: String = snapshot.getString(FORUM_QUESTION).orEmpty()
        val createdAt: Date? = snapshot.getDate(FORUM_CREATED_AT)
        val userId: String = snapshot.getString(FORUM_USER_ID).orEmpty()
        val userName: String = snapshot.getString(FORUM_USER_NAME).orEmpty()
        val status: String = snapshot.getString(FORUM_STATUS).orEmpty()
        val acceptedAnswerId: String? = snapshot.getString(FORUM_ACCEPTED_ANSWER_ID)

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

    fun mapToDiscussionForumAnswers(snapshots: MutableList<DocumentSnapshot>): MutableList<DiscussionForumAnswer> {
        return DataMapper.mapToLists(snapshots, ::mapToDiscussionForumAnswer)
    }

    fun mapToDiscussionForumAnswer(snapshot: DocumentSnapshot): DiscussionForumAnswer {
        val id: String = snapshot.id
        val answer: String = snapshot.getString(FORUM_ANSWER).orEmpty()
        val createdAt: Date? = snapshot.getDate(FORUM_CREATED_AT)
        val userId: String = snapshot.getString(FORUM_USER_ID).orEmpty()
        val userName: String = snapshot.getString(FORUM_USER_NAME).orEmpty()

        return DiscussionForumAnswer(id, answer, createdAt, userId, userName)
    }
}