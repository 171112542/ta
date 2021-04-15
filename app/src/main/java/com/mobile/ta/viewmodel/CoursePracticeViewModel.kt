package com.mobile.ta.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.ta.data.CourseOverviewData
import com.mobile.ta.data.CoursePracticeData
import com.mobile.ta.model.CourseOverview
import com.mobile.ta.model.CourseQuestion
import com.mobile.ta.model.CourseQuestionAnswer
import com.mobile.ta.repo.CourseRepository
import com.mobile.ta.utils.publishChanges
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CoursePracticeViewModel : ViewModel() {
    private var selectedAnswers = MutableLiveData<ArrayList<CourseQuestionAnswer>>(arrayListOf())
    private val courseRepository = CourseRepository()
    val allQuestionsAnswered = Transformations.map(selectedAnswers) {
        it.size == questions.value?.size
    }
    val questions = MutableLiveData<ArrayList<CourseQuestion>>()
    private var _navigateToSubmitResultPage: MutableLiveData<Boolean> = MutableLiveData(false)
    val navigateToSubmitResultPage: LiveData<Boolean>
        get() = _navigateToSubmitResultPage

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val isChapterDoneBefore = courseRepository.getIfSubmittedBefore("bWAEfZivIK6RyKitZtt4", "jy3EsIxdCVvPn8i0Wl7W")
            if (isChapterDoneBefore) {
                _navigateToSubmitResultPage.postValue(true)
                return@launch
            }
            val questionSnapshots = courseRepository.getQuestions("jy3EsIxdCVvPn8i0Wl7W").await()
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
            courseRepository.createNewSubmittedChapter("bWAEfZivIK6RyKitZtt4", "jy3EsIxdCVvPn8i0Wl7W").await()
            selectedAnswers.value?.forEach {
                courseRepository.submitQuestionResult(it, "bWAEfZivIK6RyKitZtt4", "jy3EsIxdCVvPn8i0Wl7W").await()
                if (it.selectedAnswer == it.correctAnswer) correctAnswerCount += 1
            }
            courseRepository
                .updateCorrectAnswerCount(
                    correctAnswerCount,
                    questions.value?.size ?: 0,
                    "bWAEfZivIK6RyKitZtt4",
                    "jy3EsIxdCVvPn8i0Wl7W"
                )
                .await()
            _navigateToSubmitResultPage.postValue(true)
        }
    }
}