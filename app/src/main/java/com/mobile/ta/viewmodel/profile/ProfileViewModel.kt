package com.mobile.ta.viewmodel.profile

import android.graphics.Bitmap
import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobile.ta.model.User

class ProfileViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    init {
//        _user.postValue(UserData.getUserData())
    }

    fun setUserPhoto(photo: Bitmap) {
        _user.value?.let {
            val user = User(
                it.name,
                it.isFirstTimeLogin,
                it.email,
                it.phone,
                photo,
                it.dob,
                it.bio
            )
            _user.postValue(user)
        }
    }

    fun setUserDob(calendar: Calendar) {
        _user.value?.let {
            val user = User(
                it.name,
                it.isFirstTimeLogin,
                it.email,
                it.phone,
                it.photo,
                calendar.time,
                it.bio
            )
            _user.postValue(user)
        }
    }

    fun setUser(name: String, phone: String, bio: String) {
        _user.value?.let {
            val user = User(
                name,
                it.isFirstTimeLogin,
                it.email,
                phone,
                it.photo,
                it.dob,
                bio
            )
            _user.postValue(user)
//            UserData.setUserData(user)
        }
    }
}