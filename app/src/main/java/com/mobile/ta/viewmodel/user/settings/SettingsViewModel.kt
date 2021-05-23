package com.mobile.ta.viewmodel.user.settings

import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.NotificationRepository
import com.mobile.ta.viewmodel.base.BaseViewModel
import com.mobile.ta.viewmodel.base.BaseViewModelWithAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    authRepository: AuthRepository,
    notificationRepository: NotificationRepository
) : BaseViewModelWithAuth(authRepository, notificationRepository) {

    fun doLogOut() {
        super.logOut()
    }
}