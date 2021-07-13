package com.mobile.ta.teacher.adapter.course.chapter.assignment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.databinding.TVhCourseQuestionBinding
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion

class CourseQuestionAdapter(
    diffCallback: com.mobile.ta.teacher.adapter.diff.CourseQuestionDiffCallback
): ListAdapter<AssignmentQuestion, CourseQuestionAdapter.ViewHolder>(diffCallback) {
    inner class ViewHolder(
        private val binding: TVhCourseQuestionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(assignmentQuestion: AssignmentQuestion, position: Int) {
            binding.let {
                it.tVhCourseQuestionQuestionLabel.text = it.root.context.getString(
                    R.string.question_text,
                    position + 1
                )
                it.tVhCourseQuestionQuestion.text = assignmentQuestion.question
                it.tVhCourseQuestionExplanation.text = assignmentQuestion.explanation
                it.tVhCourseQuestionChoiceOne.text = assignmentQuestion.choices[0]
                it.tVhCourseQuestionChoiceTwo.text = assignmentQuestion.choices[1]
                it.tVhCourseQuestionChoiceThree.text = assignmentQuestion.choices[2]
                it.tVhCourseQuestionChoiceFour.text = assignmentQuestion.choices[3]
                when (assignmentQuestion.correctAnswer) {
                    0 -> {
                        it.tVhCourseQuestionChoiceOne.isChecked = true
                    }
                    1 -> {
                        it.tVhCourseQuestionChoiceTwo.isChecked = true
                    }
                    2 -> {
                        it.tVhCourseQuestionChoiceThree.isChecked = true
                    }
                    3 -> {
                        it.tVhCourseQuestionChoiceFour.isChecked = true
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TVhCourseQuestionBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item, position)
    }
}