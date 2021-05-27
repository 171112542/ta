package com.mobile.ta.viewmodel.course.chapter.assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion
import com.mobile.ta.model.user.course.UserCourse
import com.mobile.ta.model.user.course.chapter.UserChapter
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
    private val courseRepository: CourseRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val chapterId = savedStateHandle.get<String>("chapterId") ?: ""
    val courseId = savedStateHandle.get<String>("courseId") ?: ""
    var chapterTitle: String = ""
    lateinit var chapter: Chapter
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
            getUserChapters(loggedInUid, courseId)
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

    private suspend fun getUserChapters(uid: String, courseId: String) {
        val userChaptersResult =
            userChapterRepository.getUserChapters(uid, courseId)
        checkStatus(userChaptersResult, {
            _userChapters.postValue(it)
        }, {
            //TODO: Add a failure handler
        })
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
            getUserChapters(loggedInUid, courseId)
            updateFinishedCourse(loggedInUid, courseId)
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