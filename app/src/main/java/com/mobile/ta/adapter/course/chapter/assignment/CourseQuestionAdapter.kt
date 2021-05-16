package com.mobile.ta.adapter.course.chapter.assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.adapter.diff.CourseQuestionDiffCallback
import com.mobile.ta.databinding.VhCourseQuestionBinding
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion

interface CourseQuestionVHListener {
    fun onSubmitAnswerListener(assignmentQuestion: AssignmentQuestion, selectedIndex: Int)
    fun onShowResultListener()
}

class CourseQuestionAdapter(
    diffCallback: CourseQuestionDiffCallback,
    val listener: CourseQuestionVHListener
) : ListAdapter<AssignmentQuestion, CourseQuestionAdapter.ViewHolder>(diffCallback) {
    companion object {
        @IntDef(QUIZ, PRACTICE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class QuestionType

        const val QUIZ = 0
        const val PRACTICE = 1
        private const val SELECTED_ANSWER_KEY = "selectedAnswer"
        private const val CORRECT_ANSWER_KEY = "correctAnswer"
    }

    private var submitResultEnabled = false
    private var selectedAnswers = listOf<MutableMap<String, Int>>()
    @QuestionType
    private var type: Int = 0

    inner class ViewHolder(private val binding: VhCourseQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(assignmentQuestion: AssignmentQuestion) {
            binding.vhCourseQuestionQuestion.text = assignmentQuestion.question
            binding.vhCourseQuestionChoiceOne.text = assignmentQuestion.choices[0]
            binding.vhCourseQuestionChoiceTwo.text = assignmentQuestion.choices[1]
            binding.vhCourseQuestionChoiceThree.text = assignmentQuestion.choices[2]
            binding.vhCourseQuestionChoiceFour.text = assignmentQuestion.choices[3]
            binding.vhCourseQuestionExplanationDescription.text = assignmentQuestion.explanation
            binding.vhCourseQuestionChoiceGroup.setOnCheckedChangeListener { _, _ ->
                binding.vhCourseQuestionSubmit.isEnabled = true
            }
            binding.vhCourseQuestionSubmit.setOnClickListener {
                disableSubmitButton()
                submitAnswer(assignmentQuestion)
                if (type == QUIZ) {
                    showSelectedAnswer(selectedAnswers[adapterPosition])
                }
                else {
                    showCorrectAnswer(selectedAnswers[adapterPosition])
                    showExplanation()
                }
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
            if (assignmentQuestion.id == currentList.last().id && submitResultEnabled) {
                showSubmitResult()
            }
            if (selectedAnswers.isNotEmpty()
                && selectedAnswers[adapterPosition][SELECTED_ANSWER_KEY] != -1
            ) {
                showCorrectAnswer(selectedAnswers[adapterPosition])
                showExplanation()
            }
        }

        private fun submitAnswer(assignmentQuestion: AssignmentQuestion) {
            val selectedAnswerId = binding.vhCourseQuestionChoiceGroup.checkedRadioButtonId
            val selectedRadioButton =
                binding.vhCourseQuestionChoiceGroup.findViewById<RadioButton>(selectedAnswerId)
            val selectedAnswerIndex =
                binding.vhCourseQuestionChoiceGroup.indexOfChild(selectedRadioButton)

            listener.onSubmitAnswerListener(assignmentQuestion, selectedAnswerIndex)
            selectedAnswers[adapterPosition][SELECTED_ANSWER_KEY] = selectedAnswerIndex
        }

        private fun disableSubmitButton() {
            binding.vhCourseQuestionSubmitGroup.visibility = View.GONE
            binding.vhCourseQuestionSubmit.isEnabled = false
        }

        private fun showSelectedAnswer(selectedAnswer: MutableMap<String, Int>) {
            val selectedAnswerIndex = selectedAnswer[SELECTED_ANSWER_KEY] ?: return
            val selectedRadioButton =
                binding.vhCourseQuestionChoiceGroup.getChildAt(selectedAnswerIndex) as RadioButton
            selectedRadioButton.isChecked = true
            selectedRadioButton.changeVisuals(
                R.drawable.drawable_rounded_rect,
                R.color.grey_light,
                R.color.grey_dark
            )
        }

        private fun showCorrectAnswer(selectedAnswer: MutableMap<String, Int>) {
            val correctAnswerIndex = selectedAnswer[CORRECT_ANSWER_KEY] ?: return
            val selectedAnswerIndex = selectedAnswer[SELECTED_ANSWER_KEY] ?: return
            val correctRadioButton =
                binding.vhCourseQuestionChoiceGroup.getChildAt(correctAnswerIndex) as RadioButton
            val selectedRadioButton =
                binding.vhCourseQuestionChoiceGroup.getChildAt(selectedAnswerIndex) as RadioButton

            selectedRadioButton.isChecked = true

            correctRadioButton.changeVisuals(
                R.drawable.drawable_rounded_rect,
                R.color.green_light,
                R.color.green
            )

            if (selectedAnswerIndex != correctAnswerIndex) {
                selectedRadioButton.changeVisuals(
                    R.drawable.drawable_rounded_rect,
                    R.color.red_light,
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

        private fun RadioButton.changeVisuals(
            backgroundDrawableId: Int,
            backgroundDrawableTintId: Int,
            tintId: Int
        ) {
            this.background = ResourcesCompat.getDrawable(
                binding.root.resources,
                backgroundDrawableId,
                null
            )
            this.backgroundTintList = ContextCompat.getColorStateList(
                binding.root.context,
                backgroundDrawableTintId
            )
            this.setTextColor(tintId)
            this.buttonTintList = ContextCompat.getColorStateList(
                binding.root.context,
                tintId
            )
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

    override fun submitList(list: MutableList<AssignmentQuestion>?) {
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

    fun setQuestionType(type: ChapterType) {
        this.type = if (type == ChapterType.QUIZ) QUIZ else PRACTICE
    }
}