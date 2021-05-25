package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.ta.model.course.chapter.discussion.DiscussionForum
import com.mobile.ta.model.course.chapter.discussion.DiscussionForumAnswer
import java.util.*

object DiscussionMapper {

    const val NAME = "name"
    const val QUESTION = "question"
    const val CREATED_AT = "createdAt"
    const val USER_ID = "userId"
    const val USER_NAME = "userName"
    const val USER_IMAGE = "userImage"
    const val STATUS = "status"
    const val ANSWER = "answer"
    const val ACCEPTED_ANSWER_ID = "acceptedAnswerId"
    const val IS_ACCEPTED = "isAccepted"

    fun mapToDiscussionForums(snapshots: QuerySnapshot): MutableList<DiscussionForum> {
        return snapshots.map {
            mapToDiscussionForum(it)
        }.toMutableList()
    }

    fun mapToDiscussionForum(snapshot: DocumentSnapshot): DiscussionForum {
        val id: String = snapshot.id
        val name: String = snapshot.getString(NAME).orEmpty()
        val question: String = snapshot.getString(QUESTION).orEmpty()
        val createdAt: Date? = snapshot.getDate(CREATED_AT)
        val userId: String = snapshot.getString(USER_ID).orEmpty()
        val userName: String = snapshot.getString(USER_NAME).orEmpty()
        val userImage: String = snapshot.getString(USER_IMAGE).orEmpty()
        val status: String = snapshot.getString(STATUS).orEmpty()
        val acceptedAnswerId: String? = snapshot.getString(ACCEPTED_ANSWER_ID)

        return DiscussionForum(
            id = id,
            name = name,
            question = question,
            createdAt = createdAt,
            userId = userId,
            userName = userName,
            status = status,
            userImage = userImage,
            acceptedAnswerId = acceptedAnswerId
        )
    }

    fun mapToDiscussionForumAnswers(snapshots: QuerySnapshot): MutableList<DiscussionForumAnswer> {
        return snapshots.map {
            mapToDiscussionForumAnswer(it)
        }.toMutableList()
    }

    private fun mapToDiscussionForumAnswer(snapshot: DocumentSnapshot): DiscussionForumAnswer {
        val id: String = snapshot.id
        val answer: String = snapshot.getString(ANSWER).orEmpty()
        val createdAt: Date? = snapshot.getDate(CREATED_AT)
        val userId: String = snapshot.getString(USER_ID).orEmpty()
        val userName: String = snapshot.getString(USER_NAME).orEmpty()
        val userImage: String = snapshot.getString(USER_IMAGE).orEmpty()
        val isAccepted: Boolean = snapshot.getBoolean(IS_ACCEPTED) ?: false

        return DiscussionForumAnswer(id, answer, createdAt, userId, userName, userImage, isAccepted)
    }
}