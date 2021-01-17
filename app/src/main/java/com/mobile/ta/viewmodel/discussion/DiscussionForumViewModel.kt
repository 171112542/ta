package com.mobile.ta.viewmodel.discussion

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mobile.ta.data.DiscussionData
import com.mobile.ta.model.discussion.DiscussionForum
import com.mobile.ta.utils.now
import java.util.Date

class DiscussionForumViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val DISCUSSION_FORUM = "DISCUSSION_FORUM"
    }

    private var _discussionForums: MutableLiveData<ArrayList<DiscussionForum>>
    val discussionForums: LiveData<ArrayList<DiscussionForum>>
        get() = _discussionForums

    init {
        _discussionForums = savedStateHandle.getLiveData(DISCUSSION_FORUM, arrayListOf())
    }

    fun createNewDiscussion(title: String, question: String) {
        val today = Date().now()
        val discussionForum =
            DiscussionForum("123", title, question, today, "NEW", "user_id", "username")
        addDiscussionForum(discussionForum)
    }

    fun fetchDiscussionForums() {
        val discussionForumsData = DiscussionData.discussionForumsData
        setDiscussionForums(discussionForumsData)
    }

    private fun addDiscussionForum(discussionForums: DiscussionForum) {
        _discussionForums.value?.add(discussionForums)
    }

    private fun setDiscussionForums(discussionForums: ArrayList<DiscussionForum>) {
        _discussionForums.value = discussionForums
    }
}