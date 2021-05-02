package com.mobile.ta.viewmodel.discussion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.mobile.ta.model.course.chapter.discussion.DiscussionForum
import com.mobile.ta.model.course.chapter.discussion.DiscussionForumAnswer
import com.mobile.ta.model.status.Status
import com.mobile.ta.repository.DiscussionRepository
import com.mobile.ta.utils.isNotNullOrBlank
import com.mobile.ta.utils.mapper.DiscussionMapper
import com.mobile.ta.utils.now
import com.mobile.ta.utils.publishChanges
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiscussionViewModel @Inject constructor(
    private val discussionRepository: DiscussionRepository
) : BaseViewModel() {

    private var _courseId: String? = null
    private val courseId: String
        get() = _courseId!!

    private var _chapterId: String? = null
    private val chapterId: String
        get() = _chapterId!!

    private var _id: String? = null
    private val id: String
        get() = _id!!

    private var _discussionAnswers = MutableLiveData<Status<MutableList<DiscussionForumAnswer>>>()
    val discussionAnswers: LiveData<Status<MutableList<DiscussionForumAnswer>>>
        get() = _discussionAnswers

    private var _discussionForumQuestion = MutableLiveData<Status<DiscussionForum>>()
    val discussionForumQuestion: LiveData<Status<DiscussionForum>>
        get() = _discussionForumQuestion

    private var _isAnswerAdded = MutableLiveData<Boolean>()
    val isAnswerAdded: LiveData<Boolean>
        get() = _isAnswerAdded

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
        if (areDataNotNull()) {
            launchViewModelScope {
                _discussionForumQuestion.postValue(
                    discussionRepository.getDiscussionForumById(courseId, chapterId, id)
                )
                _discussionAnswers.postValue(
                    discussionRepository.getDiscussionForumAnswers(courseId, chapterId, id)
                )
            }
            _discussionForumQuestion.publishChanges()
            _discussionAnswers.publishChanges()
        }
    }

    fun markAsAcceptedAnswer(answerId: String) {
        if (areDataNotNull()) {
            launchViewModelScope {
                discussionRepository.markAsAcceptedAnswer(courseId, chapterId, id, answerId)
            }
        }
    }

    fun setDiscussionData(courseId: String, chapterId: String, id: String) {
        _courseId = courseId
        _chapterId = chapterId
        _id = id
    }

    fun setIsAnswerAdded(value: Boolean) {
        _isAnswerAdded.postValue(value)
    }

    private fun addDiscussionAnswer(answer: HashMap<String, Any>) {
        if (areDataNotNull()) {
            launchViewModelScope {
                val addDiscussionResult =
                    discussionRepository.addDiscussionForumAnswer(courseId, chapterId, id, answer)
                checkStatus(addDiscussionResult.status, {
                    setIsAnswerAdded(false)
                }, {
                    setIsAnswerAdded(true)
                })
            }
            _isAnswerAdded.publishChanges()
        }
    }

    private fun areDataNotNull() =
        _id.isNotNullOrBlank() && _chapterId.isNotNullOrBlank() && _courseId.isNotNullOrBlank()
}