package com.mobile.ta.student.viewmodel.user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.user.User
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.NotificationRepository
import com.mobile.ta.repository.StudentProgressRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModelWithAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val studentProgressRepository: StudentProgressRepository,
    notificationRepository: NotificationRepository
) : BaseViewModelWithAuth(authRepository, notificationRepository) {

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean>
        get() = _isAuthenticated

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _userCourseCount = MutableLiveData<Pair<Int, Int>>()
    val userCourseCount: LiveData<Pair<Int, Int>>
        get() = _userCourseCount

    fun fetchUserData() {
        _userCourseCount.value = Pair(0, 0)
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

    fun fetchUserCourseCount(userId: String) {
        var courseCount = Pair(0, 0)
        launchViewModelScope {
            checkStatus(
                studentProgressRepository.getStudentProgressByFinished(userId, false),
                { data ->
                    courseCount = Pair(data.size, courseCount.second)
                    setCourseCount(courseCount)
                })
            checkStatus(
                studentProgressRepository.getStudentProgressByFinished(userId, true),
                { data ->
                    courseCount = Pair(courseCount.first, data.size)
                    setCourseCount(courseCount)
                })
        }
    }

    private fun setCourseCount(course: Pair<Int, Int>) {
        _userCourseCount.postValue(course)
    }

    private fun doLogOut() {
        launchViewModelScope {
            logOut()
        }
    }
}