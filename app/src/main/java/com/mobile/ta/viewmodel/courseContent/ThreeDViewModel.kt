package com.mobile.ta.viewmodel.courseContent

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.*
class ThreeDViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
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

