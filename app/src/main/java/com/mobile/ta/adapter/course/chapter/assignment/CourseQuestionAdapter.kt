package com.mobile.ta.adapter.course.chapter.assignment

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.adapter.diff.CourseQuestionDiffCallback
import com.mobile.ta.databinding.VhCourseQuestionBinding
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion
import com.mobile.ta.utils.isNull
import javax.inject.Inject
import javax.inject.Singleton

interface CourseQuestionVHListener {
    fun onSubmitAnswerListener(assignmentQuestion: AssignmentQuestion, selectedIndex: Int)
    fun onShowResultListener()
}

class CourseQuestionAdapter @Inject constructor(
    diffCallback: CourseQuestionDiffCallback
) : ListAdapter<AssignmentQuestion, CourseQuestionAdapter.ViewHolder>(diffCallback) {
    companion object {
        @IntDef(QUIZ, PRACTICE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class QuestionType

        const val QUIZ = 0
        const val PRACTICE = 1
        const val SELECTED_ANSWER_KEY = "selectedAnswer"
        const val SUBMITTED_KEY = "submitted"
        const val CORRECT_ANSWER_KEY = "correctAnswer"
    }

    lateinit var listener: CourseQuestionVHListener
    private var submitResultEnabled = false
    private var selectedAnswers = listOf<MutableMap<String, Int>>()
    @QuestionType
    private var type: Int = 0

    inner class ViewHolder(
        private val binding: VhCourseQuestionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var isBinding = false
        init {
            binding.vhCourseQuestionChoiceGroup.setOnCheckedChangeListener { group, checkedId ->
                if (isBinding) return@setOnCheckedChangeListener
                selectedAnswers[bindingAdapterPosition][SELECTED_ANSWER_KEY] =
                    group.indexOfChild(group.findViewById(checkedId))
                binding.vhCourseQuestionSubmit.isEnabled = true
            }
            binding.vhCourseQuestionSubmit.setOnClickListener {
                submitAnswer(currentList[bindingAdapterPosition])
                processSubmittedAnswer()
                checkToEnableSubmitResult()
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
        }

        fun bind(assignmentQuestion: AssignmentQuestion) {
            isBinding = true
            resetView()
            selectedAnswers[bindingAdapterPosition][SELECTED_ANSWER_KEY]?.let {
                if (it != -1) {
                    (binding.vhCourseQuestionChoiceGroup.getChildAt(it) as RadioButton)
                        .isChecked = true
                }
            }
            if (selectedAnswers[bindingAdapterPosition][SELECTED_ANSWER_KEY] != -1) {
                binding.vhCourseQuestionSubmit.isEnabled = true
            }

            binding.vhCourseQuestionQuestion.text = assignmentQuestion.question
            binding.vhCourseQuestionChoiceOne.text = assignmentQuestion.choices[0]
            binding.vhCourseQuestionChoiceTwo.text = assignmentQuestion.choices[1]
            binding.vhCourseQuestionChoiceThree.text = assignmentQuestion.choices[2]
            binding.vhCourseQuestionChoiceFour.text = assignmentQuestion.choices[3]
            binding.vhCourseQuestionExplanationDescription.text = assignmentQuestion.explanation
            if (assignmentQuestion.order == currentList.last().order && submitResultEnabled) {
                showSubmitResult()
            }
            if (selectedAnswers[bindingAdapterPosition][SUBMITTED_KEY] != 0) {
                processSubmittedAnswer()
            }
            isBinding = false
        }

        /**
         * A function to change the visuals and disable some components
         * of the ViewHolder if the question has already been answered.
         */
        private fun processSubmittedAnswer() {
            if (type == QUIZ) {
                showSelectedAnswer(selectedAnswers[bindingAdapterPosition])
            }
            else {
                showCorrectAnswer(selectedAnswers[bindingAdapterPosition])
                showExplanation()
            }
            binding.run {
                vhCourseQuestionSubmit.visibility = View.GONE
                vhCourseQuestionSubmit.isEnabled = false
                vhCourseQuestionChoiceGroup.children.forEach {
                    it.isEnabled = false
                }
            }
        }

        /**
         * A function to save the submitted answer.
         * At the same time, it will run the other listener's
         * callback for question-submitting events
         */
        private fun submitAnswer(assignmentQuestion: AssignmentQuestion) {
            val selectedAnswerId = binding.vhCourseQuestionChoiceGroup.checkedRadioButtonId
            val selectedRadioButton =
                binding.vhCourseQuestionChoiceGroup.findViewById<RadioButton>(selectedAnswerId)
            val selectedAnswerIndex =
                binding.vhCourseQuestionChoiceGroup.indexOfChild(selectedRadioButton)

            listener.onSubmitAnswerListener(assignmentQuestion, selectedAnswerIndex)
            selectedAnswers[bindingAdapterPosition][SUBMITTED_KEY] = 1
        }

        /**
         * A function to change the visuals of the radio buttons.
         * Displays the selected answer in grey.
         * Does not show which answer is correct.
         */
        private fun showSelectedAnswer(selectedAnswer: MutableMap<String, Int>) {
            val selectedAnswerIndex = selectedAnswer[SELECTED_ANSWER_KEY] ?: return
            val selectedRadioButton = when (selectedAnswerIndex) {
                0 -> binding.vhCourseQuestionChoiceOne
                1 -> binding.vhCourseQuestionChoiceTwo
                2 -> binding.vhCourseQuestionChoiceThree
                else -> binding.vhCourseQuestionChoiceFour
            }
            selectedRadioButton.changeVisuals(
                R.drawable.drawable_rounded_rect,
                R.color.grey_light,
                R.color.grey_dark,
                R.color.grey_dark
            )
        }

        /**
         * A function to change the visuals of the radio buttons.
         * Displays the selected answer.
         * Displays if answer is correct or incorrect.
         */
        private fun showCorrectAnswer(selectedAnswer: MutableMap<String, Int>) {
            val correctAnswerIndex = selectedAnswer[CORRECT_ANSWER_KEY] ?: return
            val selectedAnswerIndex = selectedAnswer[SELECTED_ANSWER_KEY] ?: return
            val correctRadioButton =
                binding.vhCourseQuestionChoiceGroup.getChildAt(correctAnswerIndex) as RadioButton
            val selectedRadioButton =
                binding.vhCourseQuestionChoiceGroup.getChildAt(selectedAnswerIndex) as RadioButton

            correctRadioButton.changeVisuals(
                R.drawable.drawable_rounded_rect,
                R.color.green_light,
                R.color.green,
                R.color.green
            )

            if (selectedAnswerIndex != correctAnswerIndex) {
                selectedRadioButton.changeVisuals(
                    R.drawable.drawable_rounded_rect,
                    R.color.red_light,
                    R.color.red_dark,
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
            textColorId: Int,
            buttonTintId: Int?
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
            this.setTextColor(textColorId)
            val radioButtonColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(
                    resources.getColor(R.color.black_60, context.theme),
                    resources.getColor(buttonTintId ?: R.color.red_dark, context.theme)
                )
            )
            this.buttonTintList = radioButtonColorStateList
        }

        /**
         * This function is used whenever a ViewHolder in this class is bound.
         * View reset is required because views are dynamically changed by user input.
         * As such, views in different adapter positions have different data.
         * The data influences how the views look.
         * As a result, reused ViewHolders must not carry the visual changes caused by
         * the data in another adapter position.
         */
        private fun resetView() {
            binding.run {
                vhCourseQuestionChoiceGroup.clearCheck()
                vhCourseQuestionChoiceGroup.children.forEach {
                    (it as RadioButton).isEnabled = true
                    it.changeVisuals(
                        R.drawable.drawable_rounded_rect,
                        R.color.white,
                        R.color.black,
                        null
                    )
                }
                vhCourseQuestionSubmit.run {
                    visibility = View.VISIBLE
                    isEnabled = false
                }
                vhCourseQuestionSubmitResult.visibility = View.GONE
                vhCourseQuestionExplanationGroup.visibility = View.GONE
                vhCourseQuestionCorrectBanner.visibility = View.GONE
                vhCourseQuestionWrongBanner.visibility = View.GONE
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

    override fun submitList(list: MutableList<AssignmentQuestion>?) {
        super.submitList(list)
        list?.let { courseQuestion ->
            selectedAnswers = courseQuestion.map {
                mutableMapOf(
                    Pair(SELECTED_ANSWER_KEY, -1),
                    Pair(CORRECT_ANSWER_KEY, it.correctAnswer),
                    Pair(SUBMITTED_KEY, 0)
                )
            }
        }
    }

    fun checkToEnableSubmitResult() {
        var submittedAnswerCount = 0
        selectedAnswers.forEach {
            if (it[SUBMITTED_KEY] != 0)
                submittedAnswerCount += 1
        }
        if (submittedAnswerCount == currentList.size) {
            submitResultEnabled = true
            notifyItemChanged(currentList.size - 1)
        }
    }

    fun setVhClickListener(listener: CourseQuestionVHListener) {
        this.listener = listener
    }

    fun setQuestionType(type: ChapterType) {
        this.type = if (type == ChapterType.QUIZ) QUIZ else PRACTICE
    }
}