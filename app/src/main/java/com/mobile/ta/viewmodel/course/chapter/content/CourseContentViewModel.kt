package com.mobile.ta.viewmodel.course.chapter.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.user.course.UserCourse
import com.mobile.ta.model.user.course.chapter.UserChapter
import com.mobile.ta.repository.*
import com.mobile.ta.utils.isNotNull
import com.mobile.ta.utils.mapper.UserChapterMapper.toHashMap
import com.mobile.ta.utils.mapper.UserCourseMapper.toHashMap
import com.mobile.ta.utils.wrapper.status.Status
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourseContentViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val chapterRepository: ChapterRepository,
    private val userCourseRepository: UserCourseRepository,
    private val userChapterRepository: UserChapterRepository,
    private val authRepository: AuthRepository
) :
    BaseViewModel() {
    private var _course = MutableLiveData<Status<Course>>()
    val course: LiveData<Status<Course>> get() = _course
    private var _chapter = MutableLiveData<Status<Chapter>>()
    val chapter: LiveData<Status<Chapter>> get() = _chapter
    private val loggedInUid = authRepository.getUser()?.uid as String

    fun getCourseChapter(courseId: String, chapterId: String) {
        launchViewModelScope {
            val courseResult = courseRepository.getCourseById(courseId)
            _course.postValue(courseResult)
            val chapterResult = chapterRepository.getChapterById(courseId, chapterId)
            _chapter.postValue(chapterResult)
        }
    }

    fun addUserChapter(courseId: String, chapterId: String) {
        launchViewModelScope {
            chapter.value?.data?.let {
                val userChapter = UserChapter(chapterId, it.title)
                userChapterRepository.addUserChapter(
                    loggedInUid,
                    courseId,
                    chapterId,
                    userChapter.toHashMap()
                )
                updateFinishedCourse(loggedInUid, courseId)
                val chapterSummary = ChapterSummary(chapterId, it.title, it.type)
                updateLastAccessedCourse(loggedInUid, courseId, chapterSummary)
            }
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

    private suspend fun updateLastAccessedCourse(
        userId: String,
        courseId: String,
        lastAccessedChapter: ChapterSummary
    ) {
        userCourseRepository.updateLastAccessedChapter(
            userId,
            courseId,
            lastAccessedChapter
        )
    }
}