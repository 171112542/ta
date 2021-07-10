package com.mobile.ta.viewmodel.base

import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.NotificationRepository

abstract class BaseViewModelWithAuth(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) : BaseViewModel() {

    protected fun logOut() {
        launchViewModelScope {
            authRepository.getUser()?.let {
                val uid = it.uid
                notificationRepository.deleteNotificationToken(uid)
                authRepository.logOut()
            }
        }
    }
}