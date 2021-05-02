package com.mobile.ta.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.mobile.ta.model.UserCourse
import com.mobile.ta.model.notification.Notification

class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.message == newItem.message
            && oldItem.notifiedAt == newItem.notifiedAt
            && oldItem.type == newItem.type
            && oldItem.hasRead == newItem.hasRead
    }
}