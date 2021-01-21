package com.mobile.ta.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.adapter.diff.CourseQuestionDiffCallback
import com.mobile.ta.databinding.VhCourseQuestionBinding
import com.mobile.ta.model.CourseQuestion
import com.mobile.ta.ui.CourseQuestionsFragmentDirections


class CourseQuestionAdapter(
        diffCallback: CourseQuestionDiffCallback
) : ListAdapter<CourseQuestion, CourseQuestionAdapter.ViewHolder>(diffCallback) {
    inner class ViewHolder(private val binding: VhCourseQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(courseQuestion: CourseQuestion) {
            binding.vhCourseQuestionQuestion.text = courseQuestion.question
            binding.vhCourseQuestionChoiceOne.text = courseQuestion.choices[0]
            binding.vhCourseQuestionChoiceTwo.text = courseQuestion.choices[1]
            binding.vhCourseQuestionChoiceThree.text = courseQuestion.choices[2]
            binding.vhCourseQuestionChoiceFour.text = courseQuestion.choices[3]
            binding.vhCourseQuestionExplanationDescription.text = courseQuestion.explanation
            binding.vhCourseQuestionChoiceGroup.setOnCheckedChangeListener { _, _ ->
                binding.vhCourseQuestionSubmit.isEnabled = true
            }
            binding.vhCourseQuestionSubmit.setOnClickListener {
                showCorrectAnswer(courseQuestion)
                binding.vhCourseQuestionSubmit.isEnabled = false
                binding.vhCourseQuestionSubmitGroup.visibility = View.GONE
                showExplanation()
            }
            binding.vhCourseQuestionShowExplanation.setOnClickListener {
                showExplanation()
            }
            binding.vhCourseQuestionHideExplanation.setOnClickListener {
                hideExplanation()
            }
            if (courseQuestion.id == currentList.last().id) {
                allowNextChapterNavigation()
            }
        }

        fun showCorrectAnswer(courseQuestion: CourseQuestion) {
            val selectedAnswerId = binding.vhCourseQuestionChoiceGroup.checkedRadioButtonId
            val selectedRadioButton = binding.vhCourseQuestionChoiceGroup.findViewById<RadioButton>(selectedAnswerId)
            val selectedAnswerIndex = binding.vhCourseQuestionChoiceGroup.indexOfChild(selectedRadioButton)
            val correctAnswerIndex = courseQuestion.correctAnswer
            val correctRadioButton = binding.vhCourseQuestionChoiceGroup.getChildAt(correctAnswerIndex) as RadioButton
            correctRadioButton.background = ResourcesCompat.getDrawable(
                binding.root.resources,
                R.drawable.drawable_rounded_rect,
                null
            )
            correctRadioButton.backgroundTintList = ContextCompat.getColorStateList(
                binding.root.context,
                R.color.green_light
            )
            correctRadioButton.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.green
                )
            )
            correctRadioButton.buttonTintList = ContextCompat.getColorStateList(
                binding.root.context,
                R.color.green
            )
            if (selectedAnswerIndex != correctAnswerIndex) {
                selectedRadioButton.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.drawable_rounded_rect,
                    null
                )
                selectedRadioButton.backgroundTintList = ContextCompat.getColorStateList(
                    binding.root.context,
                    R.color.red_light
                )
                selectedRadioButton.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.red_dark
                    )
                )
                selectedRadioButton.buttonTintList = ContextCompat.getColorStateList(
                    binding.root.context,
                    R.color.red_dark
                )
                binding.vhCourseQuestionWrongBanner.visibility = View.VISIBLE
            } else {
                binding.vhCourseQuestionCorrectBanner.visibility = View.VISIBLE
            }
            binding.vhCourseQuestionChoiceGroup.children.forEach {
                it.isEnabled = false
            }
        }

        fun hideExplanation() {
            binding.vhCourseQuestionShowExplanation.visibility = View.VISIBLE
            binding.vhCourseQuestionHideExplanation.visibility = View.GONE
            binding.vhCourseQuestionExplanationGroup.visibility = View.GONE
        }

        fun showExplanation() {
            binding.vhCourseQuestionShowExplanation.visibility = View.GONE
            binding.vhCourseQuestionHideExplanation.visibility = View.VISIBLE
            binding.vhCourseQuestionExplanationGroup.visibility = View.VISIBLE
        }

        fun allowNextChapterNavigation() {
            binding.vhCourseQuestionNextChapter.visibility = View.VISIBLE
            binding.vhCourseQuestionNextChapter.setOnClickListener {
                it.findNavController().navigate(CourseQuestionsFragmentDirections.actionCourseQuestionsFragmentToCourseContentFragment())
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = VhCourseQuestionBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }
}