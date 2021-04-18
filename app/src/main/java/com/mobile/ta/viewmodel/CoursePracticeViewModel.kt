package com.mobile.ta.viewmodel

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.ta.data.CourseOverviewData
import com.mobile.ta.data.CoursePracticeData
import com.mobile.ta.model.CourseOverview
import com.mobile.ta.model.CourseQuestion
import com.mobile.ta.model.CourseQuestionAnswer
import com.mobile.ta.repo.CourseRepository
import com.mobile.ta.repo.CourseRepository.Companion.CHAPTER_TYPE_FIELD
import com.mobile.ta.repo.UserRepository
import com.mobile.ta.utils.publishChanges
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CoursePracticeViewModel @ViewModelInject constructor(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {
    val chapterId = savedStateHandle.get<String>("chapterId") ?: ""
    val courseId = savedStateHandle.get<String>("courseId") ?: ""

    private lateinit var assignmentType: String
    private var selectedAnswers = MutableLiveData<ArrayList<CourseQuestionAnswer>>(arrayListOf())
    val allQuestionsAnswered = Transformations.map(selectedAnswers) {
        it.size == questions.value?.size
    }
    val questions = MutableLiveData<ArrayList<CourseQuestion>>()
    private var _navigateToSubmitResultPage: MutableLiveData<Boolean> = MutableLiveData(false)
    val navigateToSubmitResultPage: LiveData<Boolean>
        get() = _navigateToSubmitResultPage

    init {
        viewModelScope.launch(Dispatchers.IO) {
            assignmentType = courseRepository.getChapterById(chapterId)[CHAPTER_TYPE_FIELD] as String
            val isChapterDoneBefore = userRepository.getIfSubmittedBefore(
                courseId,
                chapterId
            )
            if (isChapterDoneBefore) {
                _navigateToSubmitResultPage.postValue(true)
                return@launch
            }
            val questionSnapshots = courseRepository.getQuestions(chapterId)
            val questions = ArrayList<CourseQuestion>()
            questionSnapshots.forEach {
                questions.add(
                    it.toObject(CourseQuestion::class.java)
                )
            }
            this@CoursePracticeViewModel.questions.postValue(questions)
        }
    }

    fun addSelectedAnswer(courseQuestion: CourseQuestion, selectedIndex: Int) {
        selectedAnswers.value?.add(
            CourseQuestionAnswer(
                courseQuestion.id,
                courseQuestion.question,
                selectedIndex,
                courseQuestion.correctAnswer,
                courseQuestion.order
            )
        )
        selectedAnswers.publishChanges()
    }

    fun submitAnswer() {
        viewModelScope.launch(Dispatchers.IO) {
            var correctAnswerCount = 0
            userRepository.createNewSubmittedChapter(
                assignmentType,
                courseId,
                chapterId
            )
            selectedAnswers.value?.forEach {
                userRepository.submitQuestionResult(it, courseId, chapterId)
                if (it.selectedAnswer == it.correctAnswer) correctAnswerCount += 1
            }
            userRepository
                .updateCorrectAnswerCount(
                    correctAnswerCount,
                    questions.value?.size ?: 0,
                    courseId,
                    chapterId
                )
            _navigateToSubmitResultPage.postValue(true)
        }
    }
}