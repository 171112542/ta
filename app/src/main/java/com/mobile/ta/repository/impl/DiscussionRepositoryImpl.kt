package com.mobile.ta.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.model.discussion.DiscussionForum
import com.mobile.ta.repository.DiscussionRepository
import com.mobile.ta.utils.getCallbackFlowList
import com.mobile.ta.utils.mapper.DiscussionMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DiscussionRepositoryImpl @Inject constructor(database: FirebaseFirestore) :
    DiscussionRepository {

    private val discussionCollection =
        database.collection(CollectionConstants.DISCUSSION_COLLECTION)

    override fun getDiscussions(): Flow<MutableList<DiscussionForum>> {
        return discussionCollection.getCallbackFlowList(DiscussionMapper::mapToDiscussionForums)
    }
}