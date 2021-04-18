package com.mobile.ta.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.mobile.ta.model.courseInfo.PrerequisiteCourse

class gitCourseInfoPrerequisitesCourseDiffCallback : DiffUtil.ItemCallback<PrerequisiteCourse>() {

    override fun areItemsTheSame(
        oldItem: PrerequisiteCourse, newItem: PrerequisiteCourse
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: PrerequisiteCourse, newItem: PrerequisiteCourse
    ): Boolean {
        return oldItem.id == newItem.id
            && oldItem.name == newItem.name
    }
}