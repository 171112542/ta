package com.mobile.ta.viewmodel.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private var _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean>
        get() = _isAuthenticated

    fun authenticateUser(token: String) {
        launchViewModelScope {
            checkStatus(authRepository.authenticateUser(token), { data ->
                _isAuthenticated.postValue(data)
            }, ::setUnauthenticated)
        }
    }

    private fun setUnauthenticated() {
        _isAuthenticated.postValue(false)
        launchViewModelScope {
            authRepository.logOut()
        }
    }
}