package com.mobile.ta.adapter.notification

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.mobile.ta.R
import com.mobile.ta.adapter.diff.NotificationDiffCallback
import com.mobile.ta.data.FeedbackData
import com.mobile.ta.databinding.VhNotificationBinding
import com.mobile.ta.databinding.VhProfileFeedbackBinding
import com.mobile.ta.model.Feedback
import com.mobile.ta.model.notification.Notification
import com.mobile.ta.utils.TimestampUtil

class NotificationAdapter(
    diffCallback: NotificationDiffCallback
) : ListAdapter<Notification, NotificationAdapter.ViewHolder>(diffCallback) {
    class ViewHolder(private val binding: VhNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification) {
            binding.apply {
                vhNotificationMessage.text = notification.message
                vhNotificationTime.text = TimestampUtil.getTimeDifferenceString(notification.notifiedAt)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}