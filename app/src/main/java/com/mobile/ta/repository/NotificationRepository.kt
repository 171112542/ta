package com.mobile.ta.repository

import com.mobile.ta.model.notification.Notification
import com.mobile.ta.utils.wrapper.status.Status

interface NotificationRepository {
    suspend fun sendNotificationToken(userId: String, token: String): Status<Boolean>

    suspend fun getNotificationList(userId: String): Status<MutableList<Notification>>

    suspend fun markAllNotificationsAsRead(userId: String): Status<Boolean>
}