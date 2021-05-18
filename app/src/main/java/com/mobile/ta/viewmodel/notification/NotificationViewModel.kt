package com.mobile.ta.viewmodel.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.notification.Notification
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.NotificationRepository
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    notificationRepository: NotificationRepository,
    authRepository: AuthRepository
) : BaseViewModel() {
    private var _notificationList: MutableLiveData<MutableList<Notification>> = MutableLiveData()
    val notificationList: LiveData<MutableList<Notification>>
        get() = _notificationList

    private lateinit var loggedInUid: String

    private var _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        launchViewModelScope {
            loggedInUid = authRepository.getUser()?.uid ?: return@launchViewModelScope
            val notificationList = notificationRepository.getNotificationList(loggedInUid)
            checkStatus(notificationList, {
                _notificationList.postValue(it)
                _isLoading.postValue(false)
            }, {
                //TODO: Handle network error
            })
            val status = notificationRepository.markAllNotificationsAsRead(loggedInUid)
        }
    }
}