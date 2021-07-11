package com.mobile.ta.teacher.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.mobile.ta.model.studentProgress.StudentProgress

class StudentProgressDiffCallback : DiffUtil.ItemCallback<StudentProgress>() {
    override fun areItemsTheSame(oldItem: StudentProgress, newItem: StudentProgress): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StudentProgress, newItem: StudentProgress): Boolean {
        return oldItem == newItem
    }
}