package com.mobile.ta.teacher.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.mobile.ta.model.user.feedback.Feedback

class ProfileFeedbackDiffCallback : DiffUtil.ItemCallback<Feedback>() {
    override fun areItemsTheSame(oldItem: Feedback, newItem: Feedback): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Feedback, newItem: Feedback): Boolean {
        return oldItem.feedbackType == newItem.feedbackType
            && oldItem.description == newItem.description
            && oldItem.createdAt == newItem.createdAt
    }
}