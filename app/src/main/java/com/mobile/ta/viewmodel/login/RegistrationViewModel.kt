package com.mobile.ta.viewmodel.login

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistrationViewModel : ViewModel() {

    private var _profilePicture = MutableLiveData<Bitmap>()
    val profilePicture: LiveData<Bitmap>
        get() = _profilePicture

    fun setProfilePicture(profilePicture: Bitmap) {
        _profilePicture.value = profilePicture
    }
}