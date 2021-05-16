package com.mobile.ta.viewmodel.user.settings

import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.NotificationRepository
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) : BaseViewModel() {

    fun logOut() {
        launchViewModelScope {
            logOut()
        }
    }
}