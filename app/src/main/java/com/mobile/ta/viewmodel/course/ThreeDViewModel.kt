package com.mobile.ta.viewmodel.course

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ThreeDViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _backButtonState = MutableLiveData<Boolean>()
    val backButtonState: LiveData<Boolean>
        get() = _backButtonState
    init {
        _backButtonState.value = true
    }
    fun toggleBackButtonState() {
        _backButtonState.postValue(!_backButtonState.value!!)
    }
    fun setBackButtonState(isVisible: Boolean) {
        _backButtonState.postValue(isVisible)
    }
}