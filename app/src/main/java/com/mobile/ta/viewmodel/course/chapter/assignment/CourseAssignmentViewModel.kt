package com.mobile.ta.viewmodel.course.chapter.assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.Timestamp
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.assignment.Assignment
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion
import com.mobile.ta.model.studentProgress.StudentAssignmentResult
import com.mobile.ta.model.studentProgress.StudentProgress
import com.mobile.ta.model.studentProgress.SubmittedAnswer
import com.mobile.ta.repository.*
import com.mobile.ta.utils.publishChanges
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlin.math.ceil

@ExperimentalCoroutinesApi
@HiltViewModel
class CourseAssignmentViewModel @Inject constructor(
    private val chapterRepository: ChapterRepository,
    private val authRepository: AuthRepository,
    private val courseRepository: CourseRepository,
    private val studentProgressRepository: StudentProgressRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val courseId = savedStateHandle.get<String>("courseId") ?: ""
    val assignmentId = savedStateHandle.get<String>("chapterId") ?: ""
    private lateinit var loggedInUid: String

    private var selectedAnswers = MutableLiveData<ArrayList<SubmittedAnswer>>(arrayListOf())

    private var _navigateToSubmitResultPage: MutableLiveData<Boolean> = MutableLiveData(false)
    val navigateToSubmitResultPage: LiveData<Boolean>
        get() = _navigateToSubmitResultPage

    private val _course = MutableLiveData<Course>()
    val course: LiveData<Course> get() = _course

    private val _assignment = MutableLiveData<Assignment>()
    val assignment: LiveData<Assignment> get() = _assignment

    private val _studentProgress = MutableLiveData<StudentProgress>()
    val studentProgress: LiveData<StudentProgress> get() = _studentProgress

    init {
        launchViewModelScope {
            val course = courseRepository.getCourseById(courseId)
            checkStatus(course, {
                _course.postValue(it)
            }, {
                //TODO: Add a failure handler
            })
            loggedInUid = authRepository.getUser()?.uid ?: return@launchViewModelScope

            handleAccessToFragment()
            initializeFragmentContent()
            _assignment.value?.let {
                studentProgressRepository.updateLastAccessedChapter(
                    loggedInUid,
                    courseId,
                    ChapterSummary(it.id, it.title, it.type)
                )
            }
            getStudentProgress(loggedInUid, courseId)
        }
    }

    /**
     * A function to handle whether the user is allowed to access the associated fragment
     * to submit their answers.
     * If this chapter has been done before, the user will be navigated
     * to the result fragment.
     * Otherwise, a new chapter will be created on the user collection
     * to mark that the user has accessed the chapter for the first time.
     */
    private suspend fun handleAccessToFragment() {
        val assignmentAlreadyFinished = studentProgressRepository.getIsChapterCompleted(
            loggedInUid,
            courseId,
            assignmentId
        )
        checkStatus(
            assignmentAlreadyFinished, {
                if (it) _navigateToSubmitResultPage.postValue(true)
            }, {
                //TODO: Add a failure handler
            }
        )
    }

    /**
     * A function to fetch all the required information for the associated fragment
     * so that the user can start doing the assignment.
     */
    private suspend fun initializeFragmentContent() {
        val networkAssignment = chapterRepository.getAssignmentById(courseId, assignmentId)
        checkStatus(
            networkAssignment, {
                _assignment.value = it
            }, {
                //TODO: Add a failure handler
            }
        )
    }

    /**
     * A function to add selected answers of the user to a local variable.
     * The local variable will be later submitted to the database if the user
     * submits the assignment.
     */
    fun addSelectedAnswer(assignmentQuestion: AssignmentQuestion, selectedIndex: Int) {
        selectedAnswers.value?.add(
            SubmittedAnswer(
                assignmentQuestion.question,
                assignmentQuestion.choices,
                assignmentQuestion.correctAnswer,
                assignmentQuestion.explanation,
                assignmentQuestion.order,
                selectedIndex
            )
        )
        selectedAnswers.publishChanges()
    }

    /**
     * A function to submit the answers of the user to the database level.
     * At the same time, update the user's course progress
     */
    fun submitAnswer() {
        launchViewModelScope {
            var correctAnswerCount = 0
            val submittedAnswers = selectedAnswers.value ?: return@launchViewModelScope
            assignment.value?.let {
                val passingGrade = it.passingGrade
                submittedAnswers.forEach { submittedAnswer ->
                    if (submittedAnswer.selectedAnswer == submittedAnswer.correctAnswer)
                        correctAnswerCount += 1
                }
                val score = ceil(correctAnswerCount * 100f / submittedAnswers.size).toInt()
                val assignmentToSubmit = StudentAssignmentResult(
                    "",
                    score,
                    Timestamp.now(),
                    passingGrade,
                    it.title,
                    it.type,
                    submittedAnswers
                )

                studentProgressRepository
                    .saveSubmittedAssignment(
                        loggedInUid,
                        courseId,
                        assignmentId,
                        assignmentToSubmit
                    )

                getStudentProgress(loggedInUid, courseId)
                _navigateToSubmitResultPage.postValue(true)
            }
        }
    }

    private suspend fun getStudentProgress(uid: String, courseId: String) {
        val studentProgressResult =
            studentProgressRepository.getStudentProgress(uid, courseId)
        checkStatus(studentProgressResult, {
            _studentProgress.postValue(it)
        }, {
            //TODO: Add a failure handler
        })
    }
}