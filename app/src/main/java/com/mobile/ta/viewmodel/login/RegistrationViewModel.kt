package com.mobile.ta.viewmodel.login

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.user.NewUser
import com.mobile.ta.model.user.UserRoleEnum
import com.mobile.ta.repository.AuthRepository
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

    private var _user = MutableLiveData<Pair<String, NewUser>>()
    val user: LiveData<Pair<String, NewUser>>
        get() = _user

    fun getAuthorizedUserData(isTeacher: Boolean) {
        launchViewModelScope {
            authRepository.getUser()?.let { firebaseUser ->
                _user.postValue(
                    Pair(
                        firebaseUser.uid, NewUser(
                            name = firebaseUser.displayName.orEmpty(),
                            email = firebaseUser.email.orEmpty(),
                            photo = firebaseUser.photoUrl,
                            role = getUserRole(isTeacher)
                        )
                    )
                )
            }
            registerUser(_user.value?.second?.name.orEmpty(), null)
        }
    }

    fun setProfilePicture(filePath: String) {
        _profilePicture.value = File(filePath)
    }

    fun setBirthDate(birthDate: Long) {
        user.value?.second?.birthDate = birthDate
    }

    fun updateUser(name: String) {
        _user.value?.let { user ->
            user.second.name = name
            launchViewModelScope {
                val response = authRepository.updateUser(user.first, user.second)
                checkStatus(response.status, {
                    _isUpdated.postValue(response.data.orFalse())
                }, {
                    _isUpdated.postValue(false)
                })
            }
        }
    }

    fun uploadImage() {
        val imageUri = Uri.fromFile(_profilePicture.value)
        _user.value?.first?.let { id ->
            launchViewModelScope {
                checkStatus(authRepository.uploadImage(id, imageUri).status, {
                    getImage(id, imageUri)
                })
            }
        }
    }

    private fun getImage(id: String, imageUri: Uri) {
        launchViewModelScope {
            authRepository.getImageUrl(id, imageUri).apply {
                checkStatus(status, {
                    _user.value?.second?.photo = data
                })
            }
        }
    }

    private fun getUserRole(isTeacher: Boolean) = if (isTeacher) {
        UserRoleEnum.ROLE_TEACHER
    } else {
        UserRoleEnum.ROLE_STUDENT
    }

    private fun registerUser(name: String, birthDate: Long?) {
        _user.value?.let { user ->
            user.second.birthDate = birthDate
            user.second.name = name

            launchViewModelScope {
                authRepository.registerUser(user.first, user.second)
            }
        }
    }
}