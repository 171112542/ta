package com.mobile.ta.student.viewmodel.user.feedback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.NotificationRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModelWithAuth
import com.mobile.ta.utils.mapper.UserMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) : BaseViewModelWithAuth(authRepository, notificationRepository) {

    private var _userId: String? = null

    private var _isFeedbackSent = MutableLiveData<Boolean>()
    val isFeedbackSent: LiveData<Boolean>
        get() = _isFeedbackSent

    fun addFeedback(type: String, description: String) {
        val feedback = hashMapOf<String, Any?>(
            UserMapper.FEEDBACK_TYPE to type,
            UserMapper.DESCRIPTION to description,
            UserMapper.CREATED_AT to Timestamp.now()
        )
        addFeedback(feedback)
    }

    fun fetchUserData() {
        launchViewModelScope {
            checkStatus(userRepository.getUser(), {
                _userId = it.id
            }, {
                doLogOut()
            })
        }
    }

    private fun addFeedback(data: HashMap<String, Any?>) {
        launchViewModelScope {
            checkStatus(userRepository.addUserFeedback(_userId.orEmpty(), data), { isSuccess ->
                _isFeedbackSent.postValue(isSuccess)
            }, {
                _isFeedbackSent.postValue(false)
            })
        }
    }

    private fun doLogOut() {
        launchViewModelScope {
            logOut()
        }
    }
}