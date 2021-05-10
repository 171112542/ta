package com.mobile.ta.viewmodel.course.chapter.discussion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.mobile.ta.model.course.chapter.discussion.DiscussionForum
import com.mobile.ta.model.user.User
import com.mobile.ta.repository.DiscussionRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.utils.isNotNullOrBlank
import com.mobile.ta.utils.mapper.DiscussionMapper
import com.mobile.ta.utils.publishChanges
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiscussionForumViewModel @Inject constructor(
    private val discussionRepository: DiscussionRepository,
    private val userRepository: UserRepository,
) : BaseViewModel() {

    private var _courseId: String? = null
    private val courseId: String
        get() = _courseId!!

    private var _chapterId: String? = null
    private val chapterId: String
        get() = _chapterId!!

    private var _discussionForums = MutableLiveData<MutableList<DiscussionForum>>()
    val discussionForums: LiveData<MutableList<DiscussionForum>>
        get() = _discussionForums

    private var _isForumAdded = MutableLiveData<Boolean>()
    val isForumAdded: LiveData<Boolean>
        get() = _isForumAdded

    private var user: User? = null

    fun createNewDiscussion(title: String, question: String) {
        val discussionForum = hashMapOf<String, Any?>(
            DiscussionMapper.NAME to title,
            DiscussionMapper.QUESTION to question,
            DiscussionMapper.CREATED_AT to Timestamp.now(),
            DiscussionMapper.USER_ID to user?.id,
            DiscussionMapper.USER_NAME to user?.name,
            DiscussionMapper.USER_IMAGE to user?.photo.orEmpty(),
            DiscussionMapper.ACCEPTED_ANSWER_ID to null
        )
        addDiscussionForum(discussionForum)
    }

    fun fetchDiscussionForums() {
        if (areDataNotNull()) {
            launchViewModelScope {
                checkStatus(discussionRepository.getDiscussionForums(courseId, chapterId), {
                    _discussionForums.postValue(it)
                })
                checkStatus(userRepository.getUser(), {
                    user = it
                })
            }
            _discussionForums.publishChanges()
        }
    }

    fun getCourseAndChapterId() = Pair(courseId, chapterId)

    fun setCourseAndChapterId(courseId: String, chapterId: String) {
        _courseId = courseId
        _chapterId = chapterId
    }

    fun setIsForumAdded(value: Boolean) {
        _isForumAdded.postValue(value)
    }

    private fun addDiscussionForum(discussionForum: HashMap<String, Any?>) {
        if (areDataNotNull()) {
            launchViewModelScope {
                checkStatus(discussionRepository.addDiscussionForum(
                    courseId, chapterId, discussionForum
                ), {
                    setIsForumAdded(true)
                }, {
                    setIsForumAdded(false)
                })
            }
            _isForumAdded.publishChanges()
        }
    }

    private fun areDataNotNull() = _courseId.isNotNullOrBlank() && _chapterId.isNotNullOrBlank()
}