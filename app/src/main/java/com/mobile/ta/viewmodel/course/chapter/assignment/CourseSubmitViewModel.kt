package com.mobile.ta.viewmodel.course.chapter.assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.user.course.chapter.assignment.UserSubmittedAssignment
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.ChapterRepository
import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.repository.UserRepository
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
    private var _showNextChapterButton: MutableLiveData<Boolean> = MutableLiveData(false)
    val showNextChapterButton: LiveData<Boolean>
        get() = _showNextChapterButton
    private var _showRetryButton: MutableLiveData<Boolean> = MutableLiveData(false)
    val showRetryButton: LiveData<Boolean>
        get() = _showRetryButton
    var nextChapterSummary: ChapterSummary? = null

    private val _course = MutableLiveData<Course>()
    val course: LiveData<Course> get() = _course

    init {
        launchViewModelScope {
            val course = courseRepository.getCourseById(courseId)
            checkStatus(course, {
                _course.postValue(it)
            }, {})
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
            val networkSubmittedAssignment = userRepository.getSubmittedChapter(loggedInUid, courseId, chapterId)
            checkStatus(
                networkSubmittedAssignment, {
                    _submittedAssignment.postValue(it)
                }, {
                    //TODO: Add a failure handler
                }
            )

            _showRetryButton.postValue(chapter.type == ChapterType.PRACTICE)
            if (nextChapterSummary?.id.isNotNull()) {
                _showNextChapterButton.postValue(true)
            }
        }
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