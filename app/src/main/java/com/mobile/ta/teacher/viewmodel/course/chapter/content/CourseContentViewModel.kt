package com.mobile.ta.teacher.viewmodel.course.chapter.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.discussion.DiscussionForum
import com.mobile.ta.model.studentProgress.StudentProgress
import com.mobile.ta.repository.*
import com.mobile.ta.utils.wrapper.status.Status
import com.mobile.ta.ui.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourseContentViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val chapterRepository: ChapterRepository,
    private val studentProgressRepository: StudentProgressRepository,
    authRepository: AuthRepository,
    private val discussionRepository: DiscussionRepository
) :
    BaseViewModel() {
    private var _course = MutableLiveData<Status<Course>>()
    val course: LiveData<Status<Course>> get() = _course
    private var _chapter = MutableLiveData<Status<Chapter>>()
    val chapter: LiveData<Status<Chapter>> get() = _chapter
    private val loggedInUid = authRepository.getUser()?.uid
    private var _discussions = MutableLiveData<Status<MutableList<DiscussionForum>>>()
    val discussions: LiveData<Status<MutableList<DiscussionForum>>> get() = _discussions
    private val _studentProgress = MutableLiveData<Status<StudentProgress>>()
    val studentProgress: LiveData<Status<StudentProgress>> get() = _studentProgress

    fun getCourseChapter(courseId: String, chapterId: String) {
        launchViewModelScope {
            getStudentProgress(courseId)
            val courseResult = courseRepository.getCourseById(courseId)
            _course.postValue(courseResult)
            val chapterResult = chapterRepository.getChapterById(courseId, chapterId)
            _chapter.postValue(chapterResult)
        }
    }

    fun updateFinishedChapter(courseId: String, chapterId: String) {
        chapter.value?.data?.let {
            launchViewModelScope {
                loggedInUid?.let { uid ->
                    studentProgress.value?.data?.finishedChapterIds.let { finishedChapters ->
                        if (finishedChapters != null)
                            studentProgressRepository.updateFinishedChapter(
                                uid,
                                courseId,
                                chapterId,
                                finishedChapters
                            )
                    }
                    val chapterSummary = ChapterSummary(
                        chapterId,
                        it.title,
                        it.type
                    )
                    updateLastAccessedCourse(uid, courseId, chapterSummary)
                    getStudentProgress(courseId)
                }
            }
        }
    }

    private suspend fun getStudentProgress(courseId: String) {
        loggedInUid?.let { uid ->
            val studentProgressResult =
                studentProgressRepository.getStudentProgress(uid, courseId)
            _studentProgress.postValue(studentProgressResult)
        }
    }

    private suspend fun updateLastAccessedCourse(
        userId: String,
        courseId: String,
        lastAccessedChapter: ChapterSummary
    ) {
        studentProgressRepository.updateLastAccessedChapter(
            userId,
            courseId,
            lastAccessedChapter
        )
    }

    fun getDiscussion(courseId: String, chapterId: String) {
        launchViewModelScope {
            _discussions.postValue(discussionRepository.getDiscussionForums(courseId, chapterId))
        }
    }
}