package com.mobile.ta.teacher.viewmodel.course.chapter.discussion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.course.chapter.discussion.DiscussionForum
import com.mobile.ta.model.user.User
import com.mobile.ta.repository.DiscussionRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModel
import com.mobile.ta.utils.isNotNullOrBlank
import com.mobile.ta.utils.publishChanges
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

    private var user: User? = null

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

    private fun areDataNotNull() = _courseId.isNotNullOrBlank() && _chapterId.isNotNullOrBlank()
}