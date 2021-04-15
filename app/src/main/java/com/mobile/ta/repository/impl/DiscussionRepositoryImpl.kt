package com.mobile.ta.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.model.discussion.DiscussionForum
import com.mobile.ta.model.discussion.DiscussionForumAnswer
import com.mobile.ta.model.status.Status
import com.mobile.ta.repository.DiscussionRepository
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.mapper.DiscussionMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DiscussionRepositoryImpl @Inject constructor(
    database: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher
) : DiscussionRepository {

    private val discussionCollection =
        database.collection(CollectionConstants.DISCUSSION_COLLECTION)

    override suspend fun getDiscussionForums(): Status<MutableList<DiscussionForum>> {
        return withContext(ioDispatcher) {
            discussionCollection.fetchData(DiscussionMapper::mapToDiscussionForums)
        }
    }

    override suspend fun getDiscussionForumById(id: String): Status<DiscussionForum> {
        return withContext(ioDispatcher) {
            discussionCollection.document(id)
                .fetchData(DiscussionMapper::mapToDiscussionForum)
        }
    }

    override suspend fun getDiscussionForumAnswers(
        discussionId: String
    ): Status<MutableList<DiscussionForumAnswer>> {
        return withContext(ioDispatcher) {
            getDiscussionAnswerCollection(discussionId)
                .fetchData(DiscussionMapper::mapToDiscussionForumAnswers)
        }
    }

    override suspend fun addDiscussionForum(data: HashMap<String, Any?>): Status<Boolean> {
        return withContext(ioDispatcher) {
            discussionCollection.add(data).fetchData()
        }
    }

    override suspend fun addDiscussionForumAnswer(
        discussionId: String,
        data: HashMap<String, Any>
    ): Status<Boolean> {
        return withContext(ioDispatcher) {
            getDiscussionAnswerCollection(discussionId).add(data).fetchData()
        }
    }

    override suspend fun markAsAcceptedAnswer(
        discussionId: String,
        answerId: String
    ): Status<Boolean> {
        return withContext(ioDispatcher) {
            discussionCollection.document(discussionId).update(
                mapOf(
                    DiscussionMapper.ACCEPTED_ANSWER_ID to answerId
                )
            ).fetchData()
            getDiscussionAnswerCollection(discussionId).document(answerId).update(
                mapOf(
                    DiscussionMapper.IS_ACCEPTED to true
                )
            ).fetchData()
        }
    }

    private fun getDiscussionAnswerCollection(discussionId: String) =
        discussionCollection.document(discussionId)
            .collection(CollectionConstants.DISCUSSION_ANSWER_COLLECTION)
}