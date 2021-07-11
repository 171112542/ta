package com.mobile.ta.teacher.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion

class CourseQuestionDiffCallback : DiffUtil.ItemCallback<AssignmentQuestion>() {
    override fun areItemsTheSame(
        oldItem: AssignmentQuestion,
        newItem: AssignmentQuestion
    ): Boolean {
        return oldItem.question == newItem.question
    }

    override fun areContentsTheSame(
        oldItem: AssignmentQuestion,
        newItem: AssignmentQuestion
    ): Boolean {
        return oldItem.question == newItem.question &&
            oldItem.choices == newItem.choices &&
            oldItem.correctAnswer == newItem.correctAnswer &&
            oldItem.explanation == newItem.explanation
    }
}