package com.mobile.ta.viewmodel.courseAssignment

import androidx.lifecycle.*
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.course.chapter.assignment.UserAssignmentAnswer
import com.mobile.ta.model.course.chapter.assignment.UserSubmittedAssignment
import com.mobile.ta.repo.ChapterRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class CourseSubmitViewModel @Inject constructor(
    private val chapterRepository: ChapterRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val courseId = savedStateHandle.get<String>("courseId") ?: ""
    private val chapterId = savedStateHandle.get<String>("chapterId") ?: ""
    var chapterTitle = ""
    private lateinit var chapter: Chapter

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
    lateinit var nextChapterType: ChapterType
    var nextChapterId: String? = null

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
            nextChapterId = chapter.nextChapterId
            val networkSubmittedAssignment = userRepository.getSubmittedChapter(courseId, chapterId)
            checkStatus(
                networkSubmittedAssignment, {
                    _submittedAssignment.postValue(it)
                }, {
                    //TODO: Add a failure handler
                }
            )

            _showRetryButton.postValue(chapter.type == ChapterType.PRACTICE)
            if (nextChapterId != null) _showNextChapterButton.postValue(true)
        }
    }

    fun retry() {
        launchViewModelScope {
            userRepository.resetSubmittedChapter(courseId, chapterId)
        }
    }

    fun navigateToNextChapter() {
        launchViewModelScope {
            nextChapterType = chapter.nextChapterType
            _navigateToNextChapter.postValue(true)
        }
    }
}