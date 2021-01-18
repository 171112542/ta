package com.mobile.ta.viewmodel.login

import android.graphics.Bitmap
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class RegistrationViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val PROFILE_PICTURE = "PROFILE_PICTURE"
    }

    private var _profilePicture: MutableLiveData<Bitmap>
    val profilePicture: LiveData<Bitmap>
        get() = _profilePicture

    init {
        _profilePicture = savedStateHandle.getLiveData(PROFILE_PICTURE)
    }

    fun setProfilePicture(profilePicture: Bitmap) {
        _profilePicture.value = profilePicture
    }
}