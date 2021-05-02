package com.mobile.ta.viewmodel.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.user.User
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.utils.isNull
import com.mobile.ta.utils.orFalse
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private var _isUpdated = MutableLiveData<Boolean>()
    val isUpdated: LiveData<Boolean>
        get() = _isUpdated

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private var _profilePicture = MutableLiveData<File>()
    val profilePicture: LiveData<File>
        get() = _profilePicture

    fun initUserData(user: User) {
        _user.value = user
    }

    fun setProfilePicture(filePath: String) {
        _profilePicture.value = File(filePath)
    }

    fun setBirthDate(birthDate: Long) {
        user.value?.birthDate = birthDate
    }

    fun updateUser(name: String, phoneNumber: String?, bio: String?) {
        _user.value?.let { user ->
            user.name = name
            user.phoneNumber = phoneNumber
            user.bio = bio

            launchViewModelScope {
                val response = userRepository.updateUser(user)
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
                    checkStatus(userRepository.uploadUserImage(user.id, imageUri).status, {
                        getImage(user.id, imageUri)
                    })
                }
            }
        }
    }

    private fun getImage(id: String, imageUri: Uri) {
        launchViewModelScope {
            userRepository.getUserImageUrl(id, imageUri).apply {
                checkStatus(status, {
                    _user.value?.photo = data.toString()
                })
            }
        }
    }
}