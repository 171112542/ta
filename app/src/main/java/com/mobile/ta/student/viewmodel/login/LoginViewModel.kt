package com.mobile.ta.student.viewmodel.login

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.NotificationRepository
import com.mobile.ta.student.viewmodel.base.BaseViewModelWithAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) : BaseViewModelWithAuth(authRepository, notificationRepository) {

    private var _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean>
        get() = _isAuthenticated

    private var _isRegistered = MutableLiveData<Boolean>()
    val isRegistered: LiveData<Boolean>
        get() = _isRegistered

    private var _isValidCredentials = MutableLiveData<Boolean>()
    val isValidCredentials: LiveData<Boolean>
        get() = _isValidCredentials

    fun checkTeacherCredential(credential: String) {
        launchViewModelScope {
            checkStatus(authRepository.checkTeacherCredentials(credential), {
                setCredentials(it)
            }, {
                setCredentials(false)
            })
        }
    }

    fun getAccountAndAuthenticateUser(data: Intent?) {
        launchViewModelScope {
            checkStatus(authRepository.getSignedInAccountFromIntent(data), {
                onSuccessGetAccount(it)
            }, ::setUnauthenticated)
        }
    }

    fun getIsUserRegistered() {
        launchViewModelScope {
            checkStatus(authRepository.getIsUserRegistered(), { data ->
                _isRegistered.postValue(data)
            }, {
                _isRegistered.postValue(false)
            })
        }
    }

    private fun onSuccessGetAccount(signedInAccount: GoogleSignInAccount?) {
        signedInAccount?.idToken?.let {
            authenticateUser(it)
        } ?: run {
            setUnauthenticated()
        }
    }

    private fun authenticateUser(token: String) {
        launchViewModelScope {
            checkStatus(authRepository.authenticateUser(token), { data ->
                _isAuthenticated.postValue(data)
            }, ::setUnauthenticated)
        }
    }

    private fun setCredentials(isValid: Boolean) {
        _isValidCredentials.postValue(isValid)
    }

    private fun setUnauthenticated() {
        _isAuthenticated.postValue(false)
        launchViewModelScope {
            logOut()
        }
    }
}