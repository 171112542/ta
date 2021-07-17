package com.mobile.ta.ui.viewmodel.login

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.mobile.ta.config.ErrorCodeConstants
import com.mobile.ta.model.user.UserRoleEnum
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.NotificationRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModelWithAuth
import com.mobile.ta.utils.isNotNull
import com.mobile.ta.utils.isNull
import com.mobile.ta.utils.orFalse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    notificationRepository: NotificationRepository
) : BaseViewModelWithAuth(authRepository, notificationRepository) {

    private var _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean>
        get() = _isAuthenticated

    private var _isRegistered = MutableLiveData<Pair<Boolean, Int?>>()
    val isRegistered: LiveData<Pair<Boolean, Int?>>
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
            checkStatus(userRepository.getUser(), { user ->
                val errorCode = when {
                    user.role.isNull() -> null
                    isTeacher(user.role) && isValidCredentials().not() ->
                        ErrorCodeConstants.ERROR_CODE_TEACHER_NEED_CREDENTIALS
                    isTeacher(user.role).not() && isValidCredentials() ->
                        ErrorCodeConstants.ERROR_CODE_STUDENT_NO_NEED_CREDENTIALS
                    else -> null
                }
                val isRegistered = user.email.isNotBlank() && errorCode.isNull()
                setIsRegistered(isRegistered, errorCode)
            }, {
                setIsRegistered(false, ErrorCodeConstants.ERROR_CODE_FAIL_TO_SIGN_IN)
            })
        }
    }

    private fun authenticateUser(token: String) {
        launchViewModelScope {
            checkStatus(authRepository.authenticateUser(token), { data ->
                _isAuthenticated.postValue(data)
            }, ::setUnauthenticated)
        }
    }

    private fun isTeacher(userRole: UserRoleEnum?) = userRole == UserRoleEnum.ROLE_TEACHER

    private fun isValidCredentials() = _isValidCredentials.value.orFalse()

    private fun onSuccessGetAccount(signedInAccount: GoogleSignInAccount?) {
        signedInAccount?.idToken?.let {
            authenticateUser(it)
        } ?: run {
            setUnauthenticated()
        }
    }

    private fun setCredentials(isValid: Boolean) {
        _isValidCredentials.postValue(isValid)
    }

    private fun setIsRegistered(isRegistered: Boolean, errorCode: Int? = null) {
        val pairIsRegistered = Pair(isRegistered, errorCode)
        _isRegistered.postValue(pairIsRegistered)
        if (errorCode.isNotNull()) {
            logOut()
        }
    }

    private fun setUnauthenticated() {
        _isAuthenticated.postValue(false)
        launchViewModelScope {
            logOut()
        }
    }
}