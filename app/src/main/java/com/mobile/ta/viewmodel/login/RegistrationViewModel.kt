package com.mobile.ta.viewmodel.login

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.user.User
import com.mobile.ta.model.user.UserRoleEnum
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.utils.isNotNull
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

    private var _profilePicture = MutableLiveData<File>()
    val profilePicture: LiveData<File>
        get() = _profilePicture

    private var _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun getAuthorizedUserData(isTeacher: Boolean) {
        launchViewModelScope {
            authRepository.getUser()?.let { firebaseUser ->
                _user.postValue(
                    User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName.orEmpty(),
                        email = firebaseUser.email.orEmpty(),
                        photo = firebaseUser.photoUrl?.toString(),
                        role = getUserRole(isTeacher),
                        phoneNumber = firebaseUser.phoneNumber
                    )
                )
                authRepository.registerUser(_user.value!!)
            }
        }
    }

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
                        getImage(user.id, imageUri)
                    })
                }
            }
        }
    }

    private fun getImage(id: String, imageUri: Uri) {
        launchViewModelScope {
            checkStatus(userRepository.getUserImageUrl(id, imageUri), { data ->
                _user.value?.photo = data.toString()
            })
        }
    }

    private fun getUserRole(isTeacher: Boolean) = if (isTeacher) {
        UserRoleEnum.ROLE_TEACHER
    } else {
        UserRoleEnum.ROLE_STUDENT
    }
}