package com.mobile.ta.viewmodel.profile

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mobile.ta.data.FeedbackData
import com.mobile.ta.model.Feedback

class ProfileFeedbackTabViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _feedbacks = MutableLiveData<List<Feedback>>()
    val feedbacks: LiveData<List<Feedback>>
        get() = _feedbacks

    init {
        _feedbacks.postValue(FeedbackData.feedbacks)
    }
}