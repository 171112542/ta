package com.mobile.ta.viewmodel.course

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mobile.ta.viewmodel.login.RegistrationViewModel
import java.util.*
import kotlin.concurrent.schedule

class ThreeDViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _timer: Timer
    private var _hasStartedTimer: Boolean

    private val _backButtonState = MutableLiveData<Boolean>()
    val backButtonState: LiveData<Boolean>
        get() = _backButtonState
    init {
        _backButtonState.value = true
//        startTimer()
        _timer = Timer()
        _hasStartedTimer = false
    }
    fun toggleBackButtonState() {
        _backButtonState.postValue(!_backButtonState.value!!)
    }
    fun setBackButtonState(isVisible: Boolean) {
        _backButtonState.postValue(isVisible)
    }
}