package com.mobile.ta.viewmodel.course.chapter.assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion
import com.mobile.ta.model.user.course.UserCourse
import com.mobile.ta.model.user.course.chapter.assignment.UserAssignmentAnswer
import com.mobile.ta.model.user.course.chapter.assignment.UserSubmittedAssignment
import com.mobile.ta.repository.*
import com.mobile.ta.utils.isNotNull
import com.mobile.ta.utils.mapper.UserCourseMapper.toHashMap
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
    private val authRepository: AuthRepository,
    private val userCourseRepository: UserCourseRepository,
    private val userChapterRepository: UserChapterRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val chapterId = savedStateHandle.get<String>("chapterId") ?: ""
    val courseId = savedStateHandle.get<String>("courseId") ?: ""
    var chapterTitle: String = ""
    private lateinit var loggedInUid: String
    lateinit var chapter: Chapter

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
            loggedInUid = authRepository.getUser()?.uid ?: return@launchViewModelScope
            val networkChapter = chapterRepository.getChapterById(courseId, chapterId)
            checkStatus(
                networkChapter, {
                    chapter = it
                    chapterTitle = chapter.title
                }, {
                    //TODO: Add a failure handler
                }
            )
            userCourseRepository.updateLastAccessedChapter(
                loggedInUid,
                courseId,
                ChapterSummary(chapter.id, chapter.title, chapter.type)
            )
            val isChapterDoneBefore = userRepository.getIfSubmittedBefore(
                loggedInUid,
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
                loggedInUid,
                courseId,
                chapterId
            )
            val userChapters = userChapterRepository.getUserChapters(loggedInUid, courseId)
            val chapters = chapterRepository.getChapters(courseId)
            val isFinished =
                if (chapters.data.isNotNull()) chapters.data?.size == userChapters.data?.size
                else false
            val userCourse = UserCourse().apply {
                finished = isFinished
            }
            userCourseRepository.addUserCourse(
                loggedInUid,
                courseId,
                userCourse.toHashMap()
            )
            selectedAnswers.value?.forEach {
                userRepository.submitQuestionResult(loggedInUid, it, courseId, chapterId)
                if (it.selectedAnswer == it.correctAnswer) correctAnswerCount += 1
            }
            val userSubmittedAssignment = UserSubmittedAssignment(
                chapter.title,
                chapter.type,
                correctAnswerCount,
                questions.value?.size ?: 0
            )
            userRepository
                .updateCorrectAnswerCount(loggedInUid, userSubmittedAssignment, courseId, chapterId)
            _navigateToSubmitResultPage.postValue(true)
        }
    }
}