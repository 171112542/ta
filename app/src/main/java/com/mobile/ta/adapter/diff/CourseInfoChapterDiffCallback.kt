package com.mobile.ta.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.mobile.ta.model.course.chapter.Chapter

class CourseInfoChapterDiffCallback : DiffUtil.ItemCallback<Chapter>() {

    override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
        return oldItem == newItem
    }
}