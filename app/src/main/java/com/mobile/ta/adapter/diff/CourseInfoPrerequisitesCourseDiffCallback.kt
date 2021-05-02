package com.mobile.ta.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.mobile.ta.model.course.chapter.info.PrerequisiteCourse

class CourseInfoPrerequisitesCourseDiffCallback : DiffUtil.ItemCallback<PrerequisiteCourse>() {

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