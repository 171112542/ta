package com.mobile.ta.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.mobile.ta.model.CourseQuestion

class CourseQuestionDiffCallback : DiffUtil.ItemCallback<CourseQuestion>() {
    override fun areItemsTheSame(oldItem: CourseQuestion, newItem: CourseQuestion): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CourseQuestion, newItem: CourseQuestion): Boolean {
        return oldItem.question == newItem.question &&
                oldItem.choices == newItem.choices &&
                oldItem.correctAnswer == newItem.correctAnswer &&
                oldItem.explanation == newItem.explanation
    }
}