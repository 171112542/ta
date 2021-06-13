package com.mobile.ta.viewmodel.course.chapter.assignment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion
import com.mobile.ta.model.user.course.UserCourse
import com.mobile.ta.model.user.course.chapter.UserChapter
import com.mobile.ta.model.user.course.chapter.assignment.UserAssignmentAnswer
import com.mobile.ta.model.user.course.chapter.assignment.UserSubmittedAssignment
import com.mobile.ta.model.user.course.chapter.assignment.showResult
import com.mobile.ta.repository.*
import com.mobile.ta.utils.isNotNull
import com.mobile.ta.utils.mapper.UserCourseMapper.toHashMap
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
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val userCourseRepository: UserCourseRepository,
    private val userChapterRepository: UserChapterRepository,
    private val courseRepository: CourseRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val chapterId = savedStateHandle.get<String>("chapterId") ?: ""
    val courseId = savedStateHandle.get<String>("courseId") ?: ""
    lateinit var chapter: Chapter
    private var submittedAssignment: UserSubmittedAssignment? = null
    private lateinit var loggedInUid: String

    private var selectedAnswers = MutableLiveData<ArrayList<UserAssignmentAnswer>>(arrayListOf())
    val questions = MutableLiveData<MutableList<AssignmentQuestion>>()

    private var _navigateToSubmitResultPage: MutableLiveData<Boolean> = MutableLiveData(false)
    val navigateToSubmitResultPage: LiveData<Boolean>
        get() = _navigateToSubmitResultPage

    private val _course = MutableLiveData<Course>()
    val course: LiveData<Course> get() = _course

    private val _userChapters = MutableLiveData<MutableList<UserChapter>>()
    val userChapters: LiveData<MutableList<UserChapter>> get() = _userChapters

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
            userCourseRepository.updateLastAccessedChapter(
                loggedInUid,
                courseId,
                ChapterSummary(chapter.id, chapter.title, chapter.type)
            )
            getUserChapters(loggedInUid, courseId)
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
        val submittedAssignment = userRepository.getSubmittedChapter(
            loggedInUid,
            courseId,
            chapterId
        )
        Log.d("CourseAssignmentViewModel", submittedAssignment.toString())
        checkStatus(
            submittedAssignment, {
                if (it.showResult()) _navigateToSubmitResultPage.postValue(true)
                else launchViewModelScope {
                    this.submittedAssignment = submittedAssignment.data
                    userRepository.createNewSubmittedAssignment(loggedInUid, courseId, chapterId)
                }
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
        val networkChapter = chapterRepository.getChapterById(courseId, chapterId)
        checkStatus(
            networkChapter, {
                chapter = it
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

    /**
     * A function to add selected answers of the user to a local variable.
     * The local variable will be later submitted to the database if the user
     * submits the assignment.
     */
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

    /**
     * A function to submit the answers of the user to the database level.
     * At the same time, update the user's course progress
     */
    fun submitAnswer() {
        launchViewModelScope {
            var correctAnswerCount = 0
            val selectedAnswers = selectedAnswers.value ?: return@launchViewModelScope
            val passingGrade = chapter.passingGrade ?: return@launchViewModelScope
            selectedAnswers.forEach {
                userRepository.submitQuestionResult(loggedInUid, it, courseId, chapterId)
                if (it.selectedAnswer == it.correctAnswer) correctAnswerCount += 1
            }
            val score = ceil(correctAnswerCount * 100f / selectedAnswers.size).toInt()
            val passed = score >= passingGrade
            val finished =
                if(chapter.type == ChapterType.QUIZ) true
                else submittedAssignment?.finished ?: false || passed
            val userSubmittedAssignment = UserSubmittedAssignment(
                chapter.title,
                chapter.type,
                score,
                passingGrade,
                passed,
                finished
            )
            userRepository
                .updateSubmittedAssignment(loggedInUid, userSubmittedAssignment, courseId, chapterId)
            val markAssignmentAsFinished =
                (chapter.type == ChapterType.PRACTICE && passed) || chapter.type == ChapterType.QUIZ
            if (markAssignmentAsFinished) {
                userRepository
                    .markAssignmentAsFinished(loggedInUid, courseId, chapterId)
            }
            _navigateToSubmitResultPage.postValue(true)

            getUserChapters(loggedInUid, courseId)
            updateFinishedCourse(loggedInUid, courseId)
        }
    }

    private suspend fun getUserChapters(uid: String, courseId: String) {
        val userChaptersResult =
            userChapterRepository.getUserChapters(uid, courseId)
        checkStatus(userChaptersResult, {
            _userChapters.postValue(it)
        }, {
            //TODO: Add a failure handler
        })
    }

    private suspend fun updateFinishedCourse(userId: String, courseId: String) {
        val userChapters = userChapterRepository.getUserChapters(userId, courseId)
        val chapters = chapterRepository.getChapters(courseId)
        val isFinished =
            if (chapters.data.isNotNull()) chapters.data?.size == userChapters.data?.size
            else false
        if (isFinished) {
            val userCourse = UserCourse().apply {
                finished = isFinished
            }
            userCourseRepository.updateFinishedCourse(
                userId,
                courseId,
                userCourse.toHashMap()
            )
        }
    }
}