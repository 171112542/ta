package com.mobile.ta.student.viewmodel.user.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.user.User
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModel
import com.mobile.ta.utils.isNotNull
import com.mobile.ta.utils.publishChanges
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

    private var _isUploaded = MutableLiveData<Boolean>()
    val isUploaded: LiveData<Boolean>
        get() = _isUploaded

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
        _user.value?.birthDate = birthDate
        _user.publishChanges()
    }

    fun updateUser(name: String, bio: String?) {
        _user.value?.let { user ->
            user.name = name
            user.bio = bio

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
        } else {
            _isUploaded.postValue(true)
        }
    }
}