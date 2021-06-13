package com.mobile.ta.viewmodel.course.chapter.assignment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.user.course.chapter.UserChapter
import com.mobile.ta.model.user.course.chapter.assignment.UserSubmittedAssignment
import com.mobile.ta.repository.*
import com.mobile.ta.utils.isNotNull
import com.mobile.ta.utils.isNull
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class CourseSubmitViewModel @Inject constructor(
    private val chapterRepository: ChapterRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val courseRepository: CourseRepository,
    private val userChapterRepository: UserChapterRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val courseId = savedStateHandle.get<String>("courseId") ?: ""
    val chapterId = savedStateHandle.get<String>("chapterId") ?: ""
    var nextChapterSummary: ChapterSummary? = null
    lateinit var chapter: Chapter
    private lateinit var loggedInUid: String

    private var _submittedAssignment: MutableLiveData<UserSubmittedAssignment> = MutableLiveData()
    val submittedAssignment: LiveData<UserSubmittedAssignment>
        get() = _submittedAssignment

    private var _navigateToNextChapter: MutableLiveData<Boolean> = MutableLiveData(false)
    val navigateToNextChapter: LiveData<Boolean>
        get() = _navigateToNextChapter

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

    private val _userChapters = MutableLiveData<MutableList<UserChapter>>()
    val userChapters: LiveData<MutableList<UserChapter>> get() = _userChapters

    init {
        launchViewModelScope {
            val course = courseRepository.getCourseById(courseId)
            checkStatus(course, {
                _course.postValue(it)
            }, {})
            loggedInUid = authRepository.getUser()?.uid ?: return@launchViewModelScope
            getUserChapters(loggedInUid, courseId)

            initializeFragmentContent()
        }
    }

    /**
     * A function to initialize the fragment content.
     * Initializing includes manipulating text data and data that affects
     * the visibility of certain elements.
     */
    private suspend fun initializeFragmentContent() {
        val networkChapter = chapterRepository.getChapterById(courseId, chapterId)
        checkStatus(
            networkChapter, {
                chapter = it
                nextChapterSummary = chapter.nextChapter
            }, {
                //TODO: Add a failure handler
            }
        )
        val networkSubmittedAssignment =
            userRepository.getSubmittedChapter(loggedInUid, courseId, chapterId)
        checkStatus(
            networkSubmittedAssignment, {
                _submittedAssignment.postValue(it)
                Log.d("Submit", it.toString())
                _showPassingGradeText.postValue(!it.passed && it.type == ChapterType.PRACTICE)
                _showRetryButton.postValue(it.type == ChapterType.PRACTICE)
                _showFinishCourseButton.postValue(nextChapterSummary?.id.isNull())
                _showNextChapterButton.postValue(nextChapterSummary?.id.isNotNull() && it.passed)
            }, {
                //TODO: Add a failure handler
            }
        )
    }


    private suspend fun getUserChapters(uid: String, courseId: String) {
        val userChaptersResult =
            userChapterRepository.getUserChapters(uid, courseId)
        checkStatus(userChaptersResult, {
            _userChapters.postValue(it)
        }, {})
    }

    fun retry() {
        launchViewModelScope {
            userRepository.createNewSubmittedAssignment(loggedInUid, courseId, chapterId)
        }
    }

    fun navigateToNextChapter() {
        launchViewModelScope {
            nextChapterSummary = chapter.nextChapter
            _navigateToNextChapter.postValue(true)
        }
    }
}