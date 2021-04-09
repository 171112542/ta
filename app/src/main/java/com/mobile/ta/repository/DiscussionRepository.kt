package com.mobile.ta.repository

import com.mobile.ta.model.discussion.DiscussionForum
import kotlinx.coroutines.flow.Flow

interface DiscussionRepository {

    fun getDiscussions(): Flow<MutableList<DiscussionForum>>
}