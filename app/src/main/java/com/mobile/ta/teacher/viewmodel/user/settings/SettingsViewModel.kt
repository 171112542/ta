package com.mobile.ta.teacher.viewmodel.user.settings

import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.NotificationRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModelWithAuth
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