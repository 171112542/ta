package com.mobile.ta.viewmodel.login

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.mobile.ta.model.user.TeacherCredential
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.utils.orFalse
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private var _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean>
        get() = _isAuthenticated

    private var _isRegistered = MutableLiveData<Boolean>()
    val isRegistered: LiveData<Boolean>
        get() = _isRegistered

    private var _teacherCredentials = MutableLiveData<Pair<Boolean, TeacherCredential?>>()
    val teacherCredentials: LiveData<Pair<Boolean, TeacherCredential?>>
        get() = _teacherCredentials

    fun checkTeacherCredential(credential: String) {
        launchViewModelScope {
            checkStatus(authRepository.checkTeacherCredentials(credential), {
                setCredentials(it)
            }, {
                setCredentials()
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
            val isRegisteredResponse = authRepository.getIsUserRegistered()
            checkStatus(isRegisteredResponse, { data ->
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

    private fun setCredentials(data: TeacherCredential? = null) {
        _teacherCredentials.postValue(Pair(data?.let {
            true
        }.orFalse(), data))
    }

    private fun setUnauthenticated() {
        _isAuthenticated.postValue(false)
        launchViewModelScope {
            authRepository.logOut()
        }
    }
}