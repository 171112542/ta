package com.mobile.ta.student.adapter.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.student.adapter.diff.NotificationDiffCallback
import com.mobile.ta.databinding.VhNotificationBinding
import com.mobile.ta.model.notification.Notification
import com.mobile.ta.model.notification.NotificationType
import com.mobile.ta.utils.TimestampUtil

class NotificationAdapter(
    diffCallback: NotificationDiffCallback
) : ListAdapter<Notification, NotificationAdapter.ViewHolder>(diffCallback) {
    class ViewHolder(private val binding: VhNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification) {
            binding.apply {
                vhNotificationMessage.text = notification.message
                vhNotificationTime.text =
                    TimestampUtil.getTimeDifferenceString(notification.notifiedAt)
                val drawableId = when (notification.hasRead) {
                    true -> R.drawable.drawable_rounded_rect
                    false -> R.drawable.drawable_new_notification_border
                }
                vhNotificationContainer.background = ContextCompat.getDrawable(
                    binding.root.context,
                    drawableId
                )
                val notificationThumbnailDrawableId = when (notification.type) {
                    NotificationType.ENROLL -> R.drawable.notification_thumbnail_enroll
                    NotificationType.UPDATE -> R.drawable.notification_thumbnail_update
                    NotificationType.REPLY -> R.drawable.notification_thumbnail_new_answer
                    NotificationType.FINISH -> R.drawable.notification_thumbnail_finish
                }
                vhNotificationThumbnail.setImageResource(notificationThumbnailDrawableId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = VhNotificationBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }
}