package com.mobile.ta.model.notification

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Notification(
    @DocumentId
    val id: String,
    val title: String,
    val message: String,
    val notifiedAt: Timestamp,
    val hasRead: Boolean,
    val type: NotificationType
) {
    constructor() : this("", "", "", Timestamp.now(), false, NotificationType.UPDATE)
}