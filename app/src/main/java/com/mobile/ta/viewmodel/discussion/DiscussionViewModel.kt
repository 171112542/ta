package com.mobile.ta.viewmodel.discussion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.mobile.ta.model.course.chapter.discussion.DiscussionForum
import com.mobile.ta.model.course.chapter.discussion.DiscussionForumAnswer
import com.mobile.ta.model.status.Status
import com.mobile.ta.repository.DiscussionRepository
import com.mobile.ta.utils.mapper.DiscussionMapper
import com.mobile.ta.utils.now
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiscussionViewModel @Inject constructor(
    private val discussionRepository: DiscussionRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    companion object {
        private const val DISCUSSION_ANSWER = "DISCUSSION_ANSWER"
        private const val DISCUSSION_QUESTION = "DISCUSSION_QUESTION"
    }

    private var id: String? = null

    private var _discussionAnswers: MutableLiveData<Status<MutableList<DiscussionForumAnswer>>>
    val discussionAnswers: LiveData<Status<MutableList<DiscussionForumAnswer>>>
        get() = _discussionAnswers

    private var _discussionForumQuestion: MutableLiveData<Status<DiscussionForum>>
    val discussionForumQuestion: LiveData<Status<DiscussionForum>>
        get() = _discussionForumQuestion

    private var _isAnswerAdded = MutableLiveData<Boolean>()
    val isAnswerAdded: LiveData<Boolean>
        get() = _isAnswerAdded

    init {
        _discussionForumQuestion = savedStateHandle.getLiveData(DISCUSSION_QUESTION)
        _discussionAnswers = savedStateHandle.getLiveData(DISCUSSION_ANSWER)
    }

    fun createNewDiscussionAnswer(answer: String) {
        val discussionForumAnswer = hashMapOf<String, Any>(
            DiscussionMapper.ANSWER to answer,
            DiscussionMapper.CREATED_AT to now(),
            DiscussionMapper.USER_ID to "userId",
            DiscussionMapper.USER_NAME to "username",
            DiscussionMapper.IS_ACCEPTED to false
        )
        addDiscussionAnswer(discussionForumAnswer)
    }

    fun fetchDiscussion() {
        id?.let { id ->
            launchViewModelScope {
                _discussionForumQuestion.postValue(discussionRepository.getDiscussionForumById(id))
                _discussionAnswers.postValue(discussionRepository.getDiscussionForumAnswers(id))
            }
        }
    }

    fun markAsAcceptedAnswer(answerId: String) {
        id?.let { discussionId ->
            launchViewModelScope {
                discussionRepository.markAsAcceptedAnswer(discussionId, answerId)
            }
        }
    }

    fun setDiscussionId(id: String) {
        this.id = id
    }

    fun setIsAnswerAdded(value: Boolean) {
        _isAnswerAdded.postValue(value)
    }

    private fun addDiscussionAnswer(answer: HashMap<String, Any>) {
        id?.let { discussionId ->
            launchViewModelScope {
                val addDiscussionResult =
                    discussionRepository.addDiscussionForumAnswer(discussionId, answer)
                checkStatus(addDiscussionResult.status, {
                    setIsAnswerAdded(false)
                }, {
                    setIsAnswerAdded(true)
                })
            }
        }
    }
}