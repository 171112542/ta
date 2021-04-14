package com.mobile.ta.viewmodel.discussion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.mobile.ta.model.discussion.DiscussionForum
import com.mobile.ta.model.status.Status
import com.mobile.ta.repository.DiscussionRepository
import com.mobile.ta.utils.mapper.DiscussionMapper
import com.mobile.ta.utils.now
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiscussionForumViewModel @Inject constructor(
    private val discussionRepository: DiscussionRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    companion object {
        private const val DISCUSSION_FORUM = "DISCUSSION_FORUM"
    }

    private var _discussionForums = MutableLiveData<Status<MutableList<DiscussionForum>>>()
    val discussionForums: LiveData<Status<MutableList<DiscussionForum>>>
        get() = _discussionForums

    private var _isForumAdded = MutableLiveData<Boolean>()
    val isForumAdded: LiveData<Boolean>
        get() = _isForumAdded

    fun createNewDiscussion(title: String, question: String) {
        val discussionForum = hashMapOf<String, Any?>(
            DiscussionMapper.FORUM_NAME to title,
            DiscussionMapper.FORUM_QUESTION to question,
            DiscussionMapper.FORUM_CREATED_AT to now(),
            DiscussionMapper.FORUM_USER_ID to "userId",
            DiscussionMapper.FORUM_USER_NAME to "username",
            DiscussionMapper.FORUM_ACCEPTED_ANSWER_ID to null
        )
        addDiscussionForum(discussionForum)
    }

    fun fetchDiscussionForums() {
        launchViewModelScope {
            _discussionForums.value = discussionRepository.getDiscussionForums()
        }
    }

    fun setIsForumAdded(value: Boolean = false) {
        _isForumAdded.value = value
    }

    private fun addDiscussionForum(discussionForum: HashMap<String, Any?>) {
        launchViewModelScope {
            val discussionForumAdded = discussionRepository.addDiscussionForum(discussionForum)
            checkStatus(discussionForumAdded.status, {
                setIsForumAdded()
            }, {
                setIsForumAdded(true)
                fetchDiscussionForums()
            })
        }
    }
}