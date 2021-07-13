package com.mobile.ta.student.viewmodel.course.chapter.assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.studentProgress.StudentAssignmentResult
import com.mobile.ta.model.studentProgress.StudentProgress
import com.mobile.ta.repository.*
import com.mobile.ta.ui.viewmodel.base.BaseViewModel
import com.mobile.ta.utils.isNotNull
import com.mobile.ta.utils.isNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class CourseSubmitViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val courseRepository: CourseRepository,
    private val studentProgressRepository: StudentProgressRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val courseId = savedStateHandle.get<String>("courseId") ?: ""
    val assignmentId = savedStateHandle.get<String>("chapterId") ?: ""
    private lateinit var loggedInUid: String

    private var _studentAssignmentResult: MutableLiveData<StudentAssignmentResult> = MutableLiveData()
    val studentAssignmentResult: LiveData<StudentAssignmentResult>
        get() = _studentAssignmentResult

    private var _navigateToNextChapter: MutableLiveData<Boolean> = MutableLiveData(false)
    val navigateToNextChapter: LiveData<Boolean>
        get() = _navigateToNextChapter

    private var _navigateToRetryPractice: MutableLiveData<Boolean> = MutableLiveData(false)
    val navigateToRetryPractice: LiveData<Boolean>
        get() = _navigateToRetryPractice

    private var _showRetryButton = MutableLiveData<Boolean>()
    val showRetryButton: LiveData<Boolean>
        get() = _showRetryButton

    private var _showNextChapterButton = MutableLiveData<Boolean>()
    val showNextChapterButton: LiveData<Boolean>
        get() = _showNextChapterButton

    private var _showFinishCourseButton = MutableLiveData<Boolean>()
    val showFinishCourseButton: LiveData<Boolean>
        get() = _showFinishCourseButton

    private var _showPassingGradeText = MutableLiveData<Boolean>()
    val showPassingGradeText: LiveData<Boolean>
        get() = _showPassingGradeText

    private val _course = MutableLiveData<Course>()
    val course: LiveData<Course> get() = _course

    private val _nextChapterSummary = MutableLiveData<ChapterSummary?>()
    val nextChapterSummary = _nextChapterSummary

    private val _studentProgress = MutableLiveData<StudentProgress>()
    val studentProgress: LiveData<StudentProgress> get() = _studentProgress

    init {
        launchViewModelScope {
            val course = courseRepository.getCourseById(courseId)
            checkStatus(course, {
                _course.postValue(it)
                val currentChapterSummary =
                    it.chapterSummaryList.find { chapterSummary -> chapterSummary.id == assignmentId }
                val currentChapterSummaryIndex =
                    it.chapterSummaryList.indexOf(currentChapterSummary)
                val nextChapterSummary =
                    if (currentChapterSummaryIndex + 1 >= it.chapterSummaryList.size) null
                    else it.chapterSummaryList[currentChapterSummaryIndex + 1]
                _nextChapterSummary.postValue(nextChapterSummary)
            }, {})
            loggedInUid = authRepository.getUser()?.uid ?: return@launchViewModelScope
            getStudentProgress(loggedInUid, courseId)

            initializeFragmentContent()
        }
    }

    /**
     * A function to initialize the fragment content.
     * Initializing includes manipulating text data and data that affects
     * the visibility of certain elements.
     */
    private suspend fun initializeFragmentContent() {
        val studentAssignmentResult =
            studentProgressRepository.getSubmittedAssignment(loggedInUid, courseId, assignmentId)
        val isAssignmentFinished = studentProgressRepository.getIsChapterCompleted(
            loggedInUid,
            courseId,
            assignmentId
        )
        var finished = false
        checkStatus(
            isAssignmentFinished, {
                finished = it
            }
        )
        checkStatus(
            studentAssignmentResult, {
                _studentAssignmentResult.postValue(it)
                _showPassingGradeText.postValue(!finished && it.type == ChapterType.PRACTICE)
                _showRetryButton.postValue(it.type == ChapterType.PRACTICE)
                _nextChapterSummary.value.let { summary ->
                    _showFinishCourseButton.postValue(
                        if (it.type == ChapterType.PRACTICE) summary.isNull() && finished
                        else summary.isNull()
                    )
                    _showNextChapterButton.postValue(
                        if (it.type == ChapterType.PRACTICE) summary.isNotNull() && finished
                        else summary.isNotNull()
                    )
                }
            }, {
                //TODO: Add a failure handler
            }
        )
    }


    private suspend fun getStudentProgress(uid: String, courseId: String) {
        val studentProgressResult =
            studentProgressRepository.getStudentProgress(uid, courseId)
        checkStatus(studentProgressResult, {
            _studentProgress.postValue(it)
        }, {})
    }

    fun retry() {
        _navigateToRetryPractice.postValue(true)
    }

    fun navigateToNextChapter() {
        launchViewModelScope {
            _navigateToNextChapter.postValue(true)
        }
    }
}