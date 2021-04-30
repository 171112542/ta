package com.mobile.ta.viewmodel.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.utils.orFalse
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
            val authenticateUserResponse = authRepository.authenticateUser(token)
            checkStatus(authenticateUserResponse.status, {
                _isAuthenticated.postValue(authenticateUserResponse.data.orFalse())
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