package com.mobile.ta.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.mobile.ta.model.course.information.RelatedCourse

class CourseInfoPrerequisitesCourseDiffCallback : DiffUtil.ItemCallback<RelatedCourse>() {

    override fun areItemsTheSame(
        oldItem: RelatedCourse, newItem: RelatedCourse
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: RelatedCourse, newItem: RelatedCourse
    ): Boolean {
        return oldItem == newItem
    }
}