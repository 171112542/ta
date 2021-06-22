package com.mobile.ta.viewmodel.course.chapter.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.user.course.UserCourse
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.ChapterRepository
import com.mobile.ta.repository.UserChapterRepository
import com.mobile.ta.repository.UserCourseRepository
import com.mobile.ta.ui.course.CourseTabFragment.CourseTabType
import com.mobile.ta.utils.wrapper.status.Status
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourseTabViewModel @Inject constructor(
    private val userCourseRepository: UserCourseRepository,
    private val userChapterRepository: UserChapterRepository,
    private val chapterRepository: ChapterRepository,
    authRepository: AuthRepository
) : BaseViewModel() {
    private val _userCourse = MutableLiveData<Status<MutableList<UserCourse>>>()
    val userCourse: LiveData<Status<MutableList<UserCourse>>> get() = _userCourse
    private val loggedInUid: String? = authRepository.getUser()?.uid

    fun getUserCourse(type: CourseTabType) {
        launchViewModelScope {
            val isFinished = type == CourseTabType.FINISHED_TAB
            loggedInUid?.let {
                _userCourse.postValue(
                    userCourseRepository.getUserCourses(
                        it,
                        isFinished
                    )
                )
            }
        }
    }

    fun getProgress(courseId: String): MutableLiveData<Double> {
        val currentProgress = MutableLiveData<Double>()
        launchViewModelScope {
            loggedInUid?.let {
                val totalUserChapter =
                    (userChapterRepository.getFinishedUserChapters(loggedInUid, courseId).data?.size
                        ?: 0).toDouble()
                val totalChapter = (chapterRepository.getChapters(courseId).data?.size ?: 0).toDouble()
                val progress = (totalUserChapter / totalChapter) * 100
                currentProgress.postValue(progress)
            }
        }
        return currentProgress
    }
}