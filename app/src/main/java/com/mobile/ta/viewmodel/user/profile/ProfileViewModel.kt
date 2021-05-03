package com.mobile.ta.viewmodel.user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.user.User
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean>
        get() = _isAuthenticated

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun fetchUserData() {
        launchViewModelScope {
            authRepository.getUser()?.let { firebaseUser ->
                getUserData(firebaseUser.uid)
                _isAuthenticated.postValue(true)
            } ?: run {
                authRepository.logOut()
                _isAuthenticated.postValue(false)
            }
        }
    }

    private suspend fun getUserData(userId: String) {
        val response = userRepository.getUserById(userId)
        checkStatus(response.status, {
            _user.postValue(response.data!!)
        })
    }
}