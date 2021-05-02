package com.mobile.ta.viewmodel.discussion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.mobile.ta.model.course.chapter.discussion.DiscussionForum
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
            DiscussionMapper.NAME to title,
            DiscussionMapper.QUESTION to question,
            DiscussionMapper.CREATED_AT to now(),
            DiscussionMapper.USER_ID to "userId",
            DiscussionMapper.USER_NAME to "username",
            DiscussionMapper.ACCEPTED_ANSWER_ID to null
        )
        addDiscussionForum(discussionForum)
    }

    fun fetchDiscussionForums() {
        launchViewModelScope {
            _discussionForums.postValue(discussionRepository.getDiscussionForums())
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
            })
        }
    }
}