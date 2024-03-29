package com.mobile.ta.student.viewmodel.user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.user.feedback.Feedback
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModel
import com.mobile.ta.utils.publishChanges
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileFeedbackTabViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _feedbacks = MutableLiveData<List<Feedback>>()
    val feedbacks: LiveData<List<Feedback>>
        get() = _feedbacks

    fun fetchFeedbacks(id: String?) {
        id?.let { userId ->
            launchViewModelScope {
                checkStatus(userRepository.getUserFeedbacks(userId), { data ->
                    _feedbacks.postValue(data)
                })
            }
            _feedbacks.publishChanges()
        }
    }
}