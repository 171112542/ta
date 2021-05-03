package com.mobile.ta.viewmodel.course.chapter.assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion
import com.mobile.ta.model.user.course.chapter.assignment.UserAssignmentAnswer
import com.mobile.ta.model.user.course.chapter.assignment.UserSubmittedAssignment
import com.mobile.ta.repository.ChapterRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.utils.publishChanges
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class CourseAssignmentViewModel @Inject constructor(
    private val chapterRepository: ChapterRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val chapterId = savedStateHandle.get<String>("chapterId") ?: ""
    val courseId = savedStateHandle.get<String>("courseId") ?: ""
    var chapterTitle: String = ""
    private lateinit var chapter: Chapter

    private var selectedAnswers = MutableLiveData<ArrayList<UserAssignmentAnswer>>(arrayListOf())
    val allQuestionsAnswered = Transformations.map(selectedAnswers) {
        it.size == questions.value?.size
    }
    val questions = MutableLiveData<MutableList<AssignmentQuestion>>()
    private var _navigateToSubmitResultPage: MutableLiveData<Boolean> = MutableLiveData(false)
    val navigateToSubmitResultPage: LiveData<Boolean>
        get() = _navigateToSubmitResultPage

    init {
        launchViewModelScope {
            val networkChapter = chapterRepository.getChapterById(courseId, chapterId)
            checkStatus(
                networkChapter, {
                    chapter = it
                    chapterTitle = chapter.title
                }, {
                    //TODO: Add a failure handler
                }
            )
            val isChapterDoneBefore = userRepository.getIfSubmittedBefore(
                courseId,
                chapterId
            )
            checkStatus(
                isChapterDoneBefore, {
                    if (it) _navigateToSubmitResultPage.postValue(true)
                }, {
                    //TODO: Add a failure handler
                }
            )

            val questionList = chapterRepository.getQuestions(courseId, chapterId)
            checkStatus(
                questionList, {
                    this.questions.postValue(it)
                }, {
                    //TODO: Add a failure handler
                }
            )
        }
    }

    fun addSelectedAnswer(assignmentQuestion: AssignmentQuestion, selectedIndex: Int) {
        selectedAnswers.value?.add(
            UserAssignmentAnswer(
                assignmentQuestion.id,
                assignmentQuestion.question,
                selectedIndex,
                assignmentQuestion.correctAnswer,
                assignmentQuestion.order
            )
        )
        selectedAnswers.publishChanges()
    }

    fun submitAnswer() {
        launchViewModelScope {
            var correctAnswerCount = 0
            userRepository.createNewSubmittedAssignment(
                courseId,
                chapterId
            )
            selectedAnswers.value?.forEach {
                userRepository.submitQuestionResult(it, courseId, chapterId)
                if (it.selectedAnswer == it.correctAnswer) correctAnswerCount += 1
            }
            val userSubmittedAssignment = UserSubmittedAssignment(
                chapter.title,
                chapter.type,
                correctAnswerCount,
                questions.value?.size ?: 0
            )
            userRepository
                .updateCorrectAnswerCount(userSubmittedAssignment, courseId, chapterId)
            _navigateToSubmitResultPage.postValue(true)
        }
    }
}