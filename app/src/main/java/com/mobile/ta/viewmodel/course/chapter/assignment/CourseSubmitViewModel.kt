package com.mobile.ta.viewmodel.course.chapter.assignment

import androidx.lifecycle.LiveData
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
    var chapterTitle = ""
    private lateinit var chapter: Chapter
    private lateinit var loggedInUid: String

    private var _submittedAssignment: MutableLiveData<UserSubmittedAssignment> = MutableLiveData()
    val submittedAssignment: LiveData<UserSubmittedAssignment>
        get() = _submittedAssignment
    private var _navigateToNextChapter: MutableLiveData<Boolean> = MutableLiveData(false)
    val navigateToNextChapter: LiveData<Boolean>
        get() = _navigateToNextChapter
    private var _hasNextChapter: MutableLiveData<Boolean> = MutableLiveData()
    val hasNextChapter: LiveData<Boolean>
        get() = _hasNextChapter
    private var _canRetry: MutableLiveData<Boolean> = MutableLiveData()
    val canRetry: LiveData<Boolean>
        get() = _canRetry
    var nextChapterSummary: ChapterSummary? = null

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
            val networkChapter = chapterRepository.getChapterById(courseId, chapterId)
            checkStatus(
                networkChapter, {
                    chapter = it
                    chapterTitle = chapter.title
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
                }, {
                    //TODO: Add a failure handler
                }
            )

            _canRetry.postValue(chapter.type == ChapterType.PRACTICE)
            _hasNextChapter.postValue(nextChapterSummary?.id.isNotNull())
        }
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
            userRepository.resetSubmittedChapter(loggedInUid, courseId, chapterId)
        }
    }

    fun navigateToNextChapter() {
        launchViewModelScope {
            nextChapterSummary = chapter.nextChapter
            _navigateToNextChapter.postValue(true)
        }
    }
}