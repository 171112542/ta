package com.mobile.ta.viewmodel.login

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.user.NewUser
import com.mobile.ta.model.user.UserRoleEnum
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.utils.isNull
import com.mobile.ta.utils.orFalse
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private var _isUpdated = MutableLiveData<Boolean>()
    val isUpdated: LiveData<Boolean>
        get() = _isUpdated

    private var _profilePicture = MutableLiveData<File>()
    val profilePicture: LiveData<File>
        get() = _profilePicture

    private var _user = MutableLiveData<NewUser>()
    val user: LiveData<NewUser>
        get() = _user

    fun getAuthorizedUserData(isTeacher: Boolean) {
        launchViewModelScope {
            authRepository.getUser()?.let { firebaseUser ->
                _user.postValue(
                    NewUser(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName.orEmpty(),
                        email = firebaseUser.email.orEmpty(),
                        photo = firebaseUser.photoUrl?.toString(),
                        role = getUserRole(isTeacher)
                    )
                )
//                _user.publishChanges()
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
                val response = authRepository.updateUser(user)
                checkStatus(response.status, {
                    _isUpdated.postValue(response.data.orFalse())
                }, {
                    _isUpdated.postValue(false)
                })
            }
        }
    }

    fun uploadImage() {
        if (_profilePicture.value.isNull().not()) {
            val imageUri = Uri.fromFile(_profilePicture.value)
            _user.value?.let { user ->
                launchViewModelScope {
                    checkStatus(authRepository.uploadImage(user.id, imageUri).status, {
                        getImage(user.id, imageUri)
                    })
                }
            }
        }
    }

    private fun getImage(id: String, imageUri: Uri) {
        launchViewModelScope {
            authRepository.getImageUrl(id, imageUri).apply {
                checkStatus(status, {
                    _user.value?.photo = data.toString()
                })
            }
        }
    }

    private fun getUserRole(isTeacher: Boolean) = if (isTeacher) {
        UserRoleEnum.ROLE_TEACHER
    } else {
        UserRoleEnum.ROLE_STUDENT
    }

    private fun registerUser(name: String) {
        _user.value?.let { user ->
            user.name = name

            launchViewModelScope {

            }
        }
    }
}