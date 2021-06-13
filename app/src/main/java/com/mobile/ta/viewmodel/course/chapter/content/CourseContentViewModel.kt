package com.mobile.ta.viewmodel.course.chapter.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.discussion.DiscussionForum
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
    private val _userChapters = MutableLiveData<Status<MutableList<UserChapter>>>()
    val userChapters: LiveData<Status<MutableList<UserChapter>>> get() = _userChapters

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
            chapter.value?.data?.let { chapter ->
                val userChapter = UserChapter(chapterId, chapter.title, true)
                loggedInUid?.let { uid ->
                    userChapterRepository.addUserChapter(
                        uid,
                        courseId,
                        chapterId,
                        userChapter.toHashMap()
                    )
                    getUserChapters(courseId)
                    updateFinishedCourse(uid, courseId)
                    val chapterSummary = ChapterSummary(chapterId, chapter.title, chapter.type)
                    updateLastAccessedCourse(uid, courseId, chapterSummary)
                }
            }
        }
    }

    private suspend fun getUserChapters(courseId: String) {
        loggedInUid?.let { uid ->
            val userChaptersResult =
                userChapterRepository.getFinishedUserChapters(uid, courseId)
            _userChapters.postValue(userChaptersResult)
        }
    }

    private suspend fun updateFinishedCourse(userId: String, courseId: String) {
        val userChapters = userChapterRepository.getFinishedUserChapters(userId, courseId)
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

    fun getDiscussion(courseId: String, chapterId: String) {
        launchViewModelScope {
            _discussions.postValue(discussionRepository.getDiscussionForums(courseId, chapterId))
        }
    }
}