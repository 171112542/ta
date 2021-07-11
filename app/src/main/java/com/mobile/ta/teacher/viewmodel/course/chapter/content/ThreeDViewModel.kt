package com.mobile.ta.teacher.viewmodel.course.chapter.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class ThreeDViewModel : ViewModel() {
    private var job: Job? = null
    private val _isVisibleBackButton = MutableLiveData(false)
    val isVisibleBackButton: LiveData<Boolean> get() = _isVisibleBackButton

    init {
        toggleBackButtonState()
    }

    fun toggleBackButtonState() {
        val isVisible = (isVisibleBackButton.value as Boolean) xor true
        _isVisibleBackButton.postValue(isVisible)
        job?.cancel()
        if (isVisible) {
            job = viewModelScope.launch(Dispatchers.IO) {
                delay(3000L)
                _isVisibleBackButton.postValue(false)
                cancel()
            }
        }
    }

    fun clearJob() {
        job?.cancel()
    }
}

