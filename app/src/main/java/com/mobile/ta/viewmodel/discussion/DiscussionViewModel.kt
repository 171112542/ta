package com.mobile.ta.viewmodel.discussion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mobile.ta.data.DiscussionData
import com.mobile.ta.model.discussion.DiscussionForum
import com.mobile.ta.model.discussion.DiscussionForumAnswer
import com.mobile.ta.utils.now
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiscussionViewModel @Inject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val DISCUSSION_ANSWER = "DISCUSSION_ANSWER"
        private const val DISCUSSION_QUESTION = "DISCUSSION_QUESTION"
    }

    private var id: String? = null

    private var _discussionAnswers: MutableLiveData<ArrayList<DiscussionForumAnswer>>
    val discussionAnswers: LiveData<ArrayList<DiscussionForumAnswer>>
        get() = _discussionAnswers

    private var _discussionForumQuestion: MutableLiveData<DiscussionForum>
    val discussionForumQuestion: LiveData<DiscussionForum>
        get() = _discussionForumQuestion

    init {
        _discussionForumQuestion = savedStateHandle.getLiveData(DISCUSSION_QUESTION)
        _discussionAnswers = savedStateHandle.getLiveData(DISCUSSION_ANSWER, arrayListOf())
    }

    fun createNewDiscussionAnswer(answer: String) {
        val today = now()
        val discussionForumAnswer = DiscussionForumAnswer(
            "ID",
            answer,
            today,
            DiscussionData.USER_ID,
            DiscussionData.USER_NAME
        )
        addDiscussionAnswer(discussionForumAnswer)
    }

    fun fetchDiscussion(id: String) {
        this.id = id
        val discussionDetail = DiscussionData.discussionForumsData.find {
            it.id == id
        }
        _discussionForumQuestion.value = discussionDetail
        _discussionAnswers.value = discussionDetail?.answer?.map { answer ->
            answer.value.apply {
                this.id = answer.key
            }
        } as ArrayList
    }

    private fun addDiscussionAnswer(answer: DiscussionForumAnswer) {
        val discussions: ArrayList<DiscussionForumAnswer> = arrayListOf()
        if (isAnswerEmpty().not()) {
            _discussionAnswers.value?.let {
                discussions.addAll(it)
            }
        }
        discussions.add(answer)
        _discussionAnswers.value = discussions
        DiscussionData.addForumAnswer(id.orEmpty(), answer)
    }

    private fun isAnswerEmpty() = _discussionAnswers.value?.isEmpty() ?: true
}