package com.mobile.ta.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.mobile.ta.model.CourseOverview

class CourseOverviewDiffCallback : DiffUtil.ItemCallback<CourseOverview>() {
    override fun areItemsTheSame(oldItem: CourseOverview, newItem: CourseOverview): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CourseOverview, newItem: CourseOverview): Boolean {
        return oldItem.description == newItem.description
                && oldItem.level == newItem.level
                && oldItem.title == newItem.title
    }
}