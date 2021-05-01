package com.mobile.ta.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mobile.ta.data.FeedbackData
import com.mobile.ta.model.Feedback
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileFeedbackTabViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _feedbacks = MutableLiveData<List<Feedback>>()
    val feedbacks: LiveData<List<Feedback>>
        get() = _feedbacks

    init {
        _feedbacks.postValue(FeedbackData.feedbacks)
    }
}