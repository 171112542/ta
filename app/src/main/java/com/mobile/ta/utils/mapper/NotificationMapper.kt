package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.QuerySnapshot
import com.mobile.ta.model.notification.Notification

object NotificationMapper {
    const val USER_NOTIFICATION_TOKEN_FIELD = "notificationToken"
    const val NOTIFICATION_HAS_READ_FIELD = "hasRead"
    const val NOTIFICATION_NOTIFIED_AT_FIELD = "notifiedAt"

    fun mapToNotifications(querySnapshot: QuerySnapshot): MutableList<Notification> =
        querySnapshot.toObjects(Notification::class.java)
}