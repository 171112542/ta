package com.mobile.ta.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobile.ta.model.Feedback

class ProfileFeedbackTabViewModel : ViewModel() {
    private val _feedbacks = MutableLiveData<List<Feedback>>()
    val feedbacks: LiveData<List<Feedback>>
        get() = _feedbacks

    init {
//        _feedbacks.postValue(FeedbackData.feedbacks)
    }
}