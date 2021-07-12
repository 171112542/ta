package com.mobile.ta.teacher.viewmodel.user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.user.User
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.repository.NotificationRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModelWithAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    notificationRepository: NotificationRepository
) : BaseViewModelWithAuth(authRepository, notificationRepository) {

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean>
        get() = _isAuthenticated

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _coursePublished = MutableLiveData<Int>()
    val coursePublished: LiveData<Int>
        get() = _coursePublished

    fun doLogOut() {
        launchViewModelScope {
            logOut()
        }
    }

    fun fetchUserData() {
        launchViewModelScope {
            checkStatus(userRepository.getUser(), { user ->
                _user.postValue(user)
                _isAuthenticated.postValue(true)
            }, {
                doLogOut()
                _isAuthenticated.postValue(false)
            })
        }
    }

    fun fetchCoursePublishedCount(userId: String) {
        launchViewModelScope {
            checkStatus(courseRepository.getTotalPublishedCourse(userId), { data ->
                _coursePublished.postValue(data)
            })
        }
    }
}