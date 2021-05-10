package com.mobile.ta.viewmodel.course.chapter.discussion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.course.chapter.discussion.DiscussionForum
import com.mobile.ta.model.course.chapter.discussion.DiscussionForumAnswer
import com.mobile.ta.model.user.User
import com.mobile.ta.repository.DiscussionRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.utils.isNotNullOrBlank
import com.mobile.ta.utils.mapper.DiscussionMapper
import com.mobile.ta.utils.now
import com.mobile.ta.utils.publishChanges
import com.mobile.ta.utils.wrapper.status.Status
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiscussionViewModel @Inject constructor(
    private val discussionRepository: DiscussionRepository,
    private val userRepository: UserRepository,
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

    private var _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun createNewDiscussionAnswer(answer: String) {
        val discussionForumAnswer = hashMapOf<String, Any>(
            DiscussionMapper.ANSWER to answer,
            DiscussionMapper.CREATED_AT to now(),
            DiscussionMapper.USER_ID to _user.value?.id.orEmpty(),
            DiscussionMapper.USER_NAME to _user.value?.name.orEmpty(),
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
                checkStatus(userRepository.getUser(), {
                    _user.postValue(it)
                })
            }
            _discussionForumQuestion.publishChanges()
            _discussionAnswers.publishChanges()
        }
    }

    fun isCurrentUser() = _user.value?.id.orEmpty() == id

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
                checkStatus(
                    discussionRepository.addDiscussionForumAnswer(
                        courseId, chapterId, id, answer
                    ), {
                        setIsAnswerAdded(it)
                    }, {
                        setIsAnswerAdded(false)
                    })
            }
            _isAnswerAdded.publishChanges()
        }
    }

    private fun areDataNotNull() =
        _id.isNotNullOrBlank() && _chapterId.isNotNullOrBlank() && _courseId.isNotNullOrBlank()
}