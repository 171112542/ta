package com.mobile.ta.viewmodel.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.notification.Notification
import com.mobile.ta.repository.NotificationRepository
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    notificationRepository: NotificationRepository
) : BaseViewModel() {
    private var _notificationList: MutableLiveData<MutableList<Notification>> = MutableLiveData()
    val notificationList: LiveData<MutableList<Notification>>
        get() = _notificationList

    private var _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        launchViewModelScope {
            val notificationList = notificationRepository.getNotificationList("l1CLTummIoarBY3Wb3FY")
            checkStatus(notificationList, {
                _notificationList.postValue(it)
                _isLoading.postValue(false)
            }, {
                //TODO: Handle network error
            })
            val status = notificationRepository.markAllNotificationsAsRead("l1CLTummIoarBY3Wb3FY")
        }
    }
}