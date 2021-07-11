package com.mobile.ta.student.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.mobile.ta.model.course.Course

class CourseDiffCallback : DiffUtil.ItemCallback<Course>() {
    override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
        return oldItem.description == newItem.description
            && oldItem.level == newItem.level
            && oldItem.title == newItem.title
    }
}