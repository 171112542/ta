package com.mobile.ta.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.mobile.ta.model.user.course.UserCourse

class UserCourseDiffCallback : DiffUtil.ItemCallback<UserCourse>() {
    override fun areItemsTheSame(oldItem: UserCourse, newItem: UserCourse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserCourse, newItem: UserCourse): Boolean {
        return oldItem == newItem
    }
}