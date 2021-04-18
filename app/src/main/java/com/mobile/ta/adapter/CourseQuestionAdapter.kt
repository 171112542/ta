package com.mobile.ta.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.adapter.diff.CourseQuestionDiffCallback
import com.mobile.ta.databinding.VhCourseQuestionBinding
import com.mobile.ta.model.CourseQuestion

interface CourseQuestionVHListener {
    fun onSubmitAnswerListener(courseQuestion: CourseQuestion, selectedIndex: Int)
    fun onShowResultListener()
}

class CourseQuestionAdapter(
        diffCallback: CourseQuestionDiffCallback,
        val listener: CourseQuestionVHListener
) : ListAdapter<CourseQuestion, CourseQuestionAdapter.ViewHolder>(diffCallback) {
    private var submitResultEnabled = false
    private var selectedAnswers = listOf<MutableMap<String, Int>>()

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
                submitAnswer(courseQuestion)
                showCorrectAnswer(selectedAnswers[adapterPosition])
                showExplanation()
            }
            binding.vhCourseQuestionShowExplanation.setOnClickListener {
                showExplanation()
            }
            binding.vhCourseQuestionHideExplanation.setOnClickListener {
                hideExplanation()
            }
            binding.vhCourseQuestionSubmitResult.setOnClickListener {
                listener.onShowResultListener()
            }
            if (courseQuestion.id == currentList.last().id && submitResultEnabled) {
                showSubmitResult()
            }
            if (selectedAnswers.isNotEmpty()
                && selectedAnswers[adapterPosition][SELECTED_ANSWER_KEY] != -1) {
                showCorrectAnswer(selectedAnswers[adapterPosition])
                showExplanation()
            }
        }

        private fun submitAnswer(courseQuestion: CourseQuestion) {
            val selectedAnswerId = binding.vhCourseQuestionChoiceGroup.checkedRadioButtonId
            val selectedRadioButton = binding.vhCourseQuestionChoiceGroup.findViewById<RadioButton>(selectedAnswerId)
            val selectedAnswerIndex = binding.vhCourseQuestionChoiceGroup.indexOfChild(selectedRadioButton)

            listener.onSubmitAnswerListener(courseQuestion, selectedAnswerIndex)
            selectedAnswers[adapterPosition][SELECTED_ANSWER_KEY] = selectedAnswerIndex
        }

        private fun showCorrectAnswer(selectedAnswer: MutableMap<String, Int>) {
            val correctAnswerIndex = selectedAnswer[CORRECT_ANSWER_KEY] ?: return
            val selectedAnswerIndex = selectedAnswer[SELECTED_ANSWER_KEY] ?: return
            val correctRadioButton = binding.vhCourseQuestionChoiceGroup.getChildAt(correctAnswerIndex) as RadioButton
            val selectedRadioButton = binding.vhCourseQuestionChoiceGroup.getChildAt(selectedAnswerIndex) as RadioButton

            binding.vhCourseQuestionSubmit.isEnabled = false
            binding.vhCourseQuestionSubmitGroup.visibility = View.GONE
            selectedRadioButton.isChecked = true

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

        private fun hideExplanation() {
            binding.vhCourseQuestionShowExplanation.visibility = View.VISIBLE
            binding.vhCourseQuestionHideExplanation.visibility = View.GONE
            binding.vhCourseQuestionExplanationGroup.visibility = View.GONE
        }

        private fun showExplanation() {
            binding.vhCourseQuestionShowExplanation.visibility = View.GONE
            binding.vhCourseQuestionHideExplanation.visibility = View.VISIBLE
            binding.vhCourseQuestionExplanationGroup.visibility = View.VISIBLE
        }

        private fun showSubmitResult() {
            binding.vhCourseQuestionSubmitResult.visibility = View.VISIBLE
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

    override fun submitList(list: MutableList<CourseQuestion>?) {
        super.submitList(list)
        list?.let { courseQuestion ->
            selectedAnswers = courseQuestion.map {
                mutableMapOf(
                    Pair(SELECTED_ANSWER_KEY, -1),
                    Pair(CORRECT_ANSWER_KEY, it.correctAnswer)
                )
            }
        }
    }

    fun enableSubmitResult() {
        submitResultEnabled = true
        notifyItemChanged(currentList.size - 1)
    }

    companion object {
        private const val SELECTED_ANSWER_KEY = "selectedAnswer"
        private const val CORRECT_ANSWER_KEY = "correctAnswer"
    }
}