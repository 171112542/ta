package com.mobile.ta.viewmodel.course.information

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
class CourseInformationViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val chapterRepository: ChapterRepository
) : BaseViewModel() {
    private val _course = MutableLiveData<Status<Course>>()
    val course: LiveData<Status<Course>>
        get() = _course
    private val _chapters = MutableLiveData<Status<MutableList<Chapter>>>()
    val chapters: LiveData<Status<MutableList<Chapter>>> get() = _chapters

    fun getCourse(courseId: String) {
        launchViewModelScope {
            val result = courseRepository.getCourseById(courseId)
            _course.postValue(result)
        }
    }

    fun getChapters(courseId: String) {
        launchViewModelScope {
            val result = chapterRepository.getChapters(courseId)
            _chapters.postValue(result)
        }
    }
}