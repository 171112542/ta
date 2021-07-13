package com.mobile.ta.teacher.viewmodel.course.chapter.assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.Timestamp
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.assignment.Assignment
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion
import com.mobile.ta.model.studentProgress.StudentAssignmentResult
import com.mobile.ta.model.studentProgress.SubmittedAnswer
import com.mobile.ta.repository.*
import com.mobile.ta.ui.viewmodel.base.BaseViewModel
import com.mobile.ta.utils.publishChanges
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
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val courseId = savedStateHandle.get<String>("courseId") ?: ""
    private val assignmentId = savedStateHandle.get<String>("chapterId") ?: ""

    private val _course = MutableLiveData<Course>()
    val course: LiveData<Course> get() = _course

    private val _assignment = MutableLiveData<Assignment>()
    val assignment: LiveData<Assignment> get() = _assignment

    private val _previousChapterSummary = MutableLiveData<ChapterSummary?>()
    val previousChapterSummary = _previousChapterSummary

    private val _nextChapterSummary = MutableLiveData<ChapterSummary?>()
    val nextChapterSummary = _nextChapterSummary

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
                val previousChapterSummary =
                    if (currentChapterSummaryIndex - 1 < 0) null
                    else it.chapterSummaryList[currentChapterSummaryIndex - 1]
                _nextChapterSummary.postValue(nextChapterSummary)
                _previousChapterSummary.postValue(previousChapterSummary)
            }, {
                //TODO: Add a failure handler
            })
            val assignment = chapterRepository.getAssignmentById(courseId, assignmentId)
            checkStatus(
                assignment, {
                    _assignment.postValue(it)
                }, {
                    //TODO: Add a failure handler
                }
            )
        }
    }
}