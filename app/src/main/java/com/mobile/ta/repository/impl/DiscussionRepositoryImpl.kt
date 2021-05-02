package com.mobile.ta.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.model.course.chapter.discussion.DiscussionForum
import com.mobile.ta.model.course.chapter.discussion.DiscussionForumAnswer
import com.mobile.ta.model.status.Status
import com.mobile.ta.repository.DiscussionRepository
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.mapper.DiscussionMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DiscussionRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore
) : DiscussionRepository {

    override suspend fun getDiscussionForums(
        courseId: String, chapterId: String
    ): Status<MutableList<DiscussionForum>> {
        return getDiscussionCollection(
            courseId,
            chapterId
        ).fetchData(DiscussionMapper::mapToDiscussionForums)
    }

    override suspend fun getDiscussionForumById(
        courseId: String, chapterId: String, id: String
    ): Status<DiscussionForum> {
        return getDiscussionCollection(courseId, chapterId).document(id)
            .fetchData(DiscussionMapper::mapToDiscussionForum)
    }

    override suspend fun getDiscussionForumAnswers(
        courseId: String, chapterId: String, discussionId: String
    ): Status<MutableList<DiscussionForumAnswer>> {
        return getDiscussionAnswerCollection(courseId, chapterId, discussionId)
            .fetchData(DiscussionMapper::mapToDiscussionForumAnswers)
    }

    override suspend fun addDiscussionForum(
        courseId: String, chapterId: String, data: HashMap<String, Any?>
    ): Status<Boolean> {
        return getDiscussionCollection(courseId, chapterId).add(data).fetchData()
    }

    override suspend fun addDiscussionForumAnswer(
        courseId: String, chapterId: String, discussionId: String, data: HashMap<String, Any>
    ): Status<Boolean> {
        return getDiscussionAnswerCollection(courseId, chapterId, discussionId).add(data)
            .fetchData()
    }

    override suspend fun markAsAcceptedAnswer(
        courseId: String, chapterId: String, discussionId: String, answerId: String
    ): Status<Boolean> {
        getDiscussionCollection(courseId, chapterId).document(discussionId).update(
            mapOf(
                DiscussionMapper.ACCEPTED_ANSWER_ID to answerId
            )
        ).fetchData()
        return getDiscussionAnswerCollection(courseId, chapterId, discussionId).document(answerId)
            .update(
                mapOf(
                    DiscussionMapper.IS_ACCEPTED to true
                )
            ).fetchData()
    }

    private fun getDiscussionCollection(courseId: String, chapterId: String) =
        database.collection(CollectionConstants.COURSE_COLLECTION)
            .document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION)
            .document(chapterId)
            .collection(CollectionConstants.DISCUSSION_COLLECTION)


    private fun getDiscussionAnswerCollection(
        courseId: String,
        chapterId: String,
        discussionId: String
    ) = getDiscussionCollection(courseId, chapterId).document(discussionId)
        .collection(CollectionConstants.DISCUSSION_ANSWER_COLLECTION)
}