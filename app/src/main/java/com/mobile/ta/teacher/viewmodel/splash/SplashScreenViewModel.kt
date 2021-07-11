package com.mobile.ta.teacher.viewmodel.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.user.UserRoleEnum
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.NotificationRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModelWithAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) : BaseViewModelWithAuth(authRepository, notificationRepository) {

    private var _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean>
        get() = _isAuthenticated

    private var _isTeacher = MutableLiveData<Boolean>()
    val isTeacher: LiveData<Boolean>
        get() = _isTeacher

    fun authenticateUser(token: String) {
        launchViewModelScope {
            checkStatus(authRepository.authenticateUser(token), { data ->
                _isAuthenticated.postValue(data)
            }, ::setUnauthenticated)
        }
    }

    fun checkUserRole() {
        launchViewModelScope {
            checkStatus(userRepository.getUser(), { user ->
                _isTeacher.postValue(user.role == UserRoleEnum.ROLE_TEACHER)
            }, ::setUnauthenticated)
        }
    }

    private fun setUnauthenticated() {
        _isAuthenticated.postValue(false)
        launchViewModelScope {
            logOut()
        }
    }
}