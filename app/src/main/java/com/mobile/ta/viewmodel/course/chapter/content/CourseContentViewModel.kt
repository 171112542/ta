package com.mobile.ta.viewmodel.course.chapter.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.repository.ChapterRepository
import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.utils.wrapper.status.Status
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourseContentViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val chapterRepository: ChapterRepository
) :
    BaseViewModel() {
    private var _course = MutableLiveData<Status<Course>>()
    val course: LiveData<Status<Course>> get() = _course
    private var _chapter = MutableLiveData<Status<Chapter>>()
    val chapter: LiveData<Status<Chapter>> get() = _chapter

    fun getCourse(courseId: String) {
        launchViewModelScope {
            val result = courseRepository.getCourseById(courseId)
            _course.postValue(result)
        }
    }

    fun getChapter(courseId: String, chapterId: String) {
        launchViewModelScope {
            val result = chapterRepository.getChapterById(courseId, chapterId)
            _chapter.postValue(result)
        }
    }
}