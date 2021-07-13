package com.mobile.ta.teacher.viewmodel.course.chapter.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.discussion.DiscussionForum
import com.mobile.ta.repository.ChapterRepository
import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.repository.DiscussionRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModel
import com.mobile.ta.utils.wrapper.status.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourseContentViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val chapterRepository: ChapterRepository,
    private val discussionRepository: DiscussionRepository
) :
    BaseViewModel() {
    private var _course = MutableLiveData<Status<Course>>()
    val course: LiveData<Status<Course>> get() = _course
    private var _chapter = MutableLiveData<Status<Chapter>>()
    val chapter: LiveData<Status<Chapter>> get() = _chapter
    private var _discussions = MutableLiveData<Status<MutableList<DiscussionForum>>>()
    val discussions: LiveData<Status<MutableList<DiscussionForum>>> get() = _discussions

    fun getCourseChapter(courseId: String, chapterId: String) {
        launchViewModelScope {
            val courseResult = courseRepository.getCourseById(courseId)
            _course.postValue(courseResult)
            val chapterResult = chapterRepository.getChapterById(courseId, chapterId)
            _chapter.postValue(chapterResult)
        }
    }

    fun getDiscussion(courseId: String, chapterId: String) {
        launchViewModelScope {
            _discussions.postValue(discussionRepository.getDiscussionForums(courseId, chapterId))
        }
    }
}