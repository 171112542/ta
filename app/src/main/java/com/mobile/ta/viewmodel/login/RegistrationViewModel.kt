package com.mobile.ta.viewmodel.login

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.mobile.ta.model.user.User
import com.mobile.ta.model.user.UserRoleEnum
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.utils.isNotNull
import com.mobile.ta.utils.publishChanges
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private var _isUpdated = MutableLiveData<Boolean>()
    val isUpdated: LiveData<Boolean>
        get() = _isUpdated

    private var _isUploaded = MutableLiveData<Boolean>()
    val isUploaded: LiveData<Boolean>
        get() = _isUploaded

    private var _profilePicture = MutableLiveData<File>()
    val profilePicture: LiveData<File>
        get() = _profilePicture

    private var _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private var isTeacher: Boolean = false

    private lateinit var initialUserData: User

    fun getAuthorizedUserData(isTeacher: Boolean) {
        setIsTeacher(isTeacher)
        launchViewModelScope {
            authRepository.getUser()?.let { firebaseUser ->
                setUserValue(isTeacher, firebaseUser)
                checkStatus(authRepository.registerUser(initialUserData), { status ->
                    if (status) {
                        _user.postValue(initialUserData)
                    }
                })
            }
        }
        _user.publishChanges()
    }

    fun getIsTeacher() = isTeacher

    fun setProfilePicture(filePath: String) {
        _profilePicture.value = File(filePath)
    }

    fun setBirthDate(birthDate: Long) {
        user.value?.birthDate = birthDate
    }

    fun updateUser(name: String) {
        _user.value?.let { user ->
            user.name = name
            launchViewModelScope {
                checkStatus(userRepository.updateUser(user), { data ->
                    _isUpdated.postValue(data)
                }, {
                    _isUpdated.postValue(false)
                })
            }
        }
    }

    fun uploadImage() {
        if (_profilePicture.value.isNotNull()) {
            val imageUri = Uri.fromFile(_profilePicture.value)
            _user.value?.let { user ->
                launchViewModelScope {
                    checkStatus(userRepository.uploadUserImage(user.id, imageUri), {
                        _user.value?.photo = it.toString()
                        _isUploaded.postValue(true)
                    }, {
                        _isUploaded.postValue(false)
                    })
                }
            }
        }
    }

    private fun getUserRole(isTeacher: Boolean) = if (isTeacher) {
        UserRoleEnum.ROLE_TEACHER
    } else {
        UserRoleEnum.ROLE_STUDENT
    }

    private fun setIsTeacher(isTeacher: Boolean) {
        this.isTeacher = isTeacher
    }

    private fun setUserValue(isTeacher: Boolean, firebaseUser: FirebaseUser) {
        initialUserData = User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName.orEmpty(),
            email = firebaseUser.email.orEmpty(),
            photo = firebaseUser.photoUrl?.toString(),
            role = getUserRole(isTeacher)
        )
    }
}