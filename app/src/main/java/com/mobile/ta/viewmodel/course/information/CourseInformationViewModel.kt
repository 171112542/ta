package com.mobile.ta.viewmodel.course.information

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.course.information.Chapter
import com.mobile.ta.model.course.information.Course
import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.utils.wrapper.status.Status
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourseInformationViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : BaseViewModel() {
    private val _course = MutableLiveData<Status<Course>>()
    val course: LiveData<Status<Course>>
        get() = _course
    private val _chapters = MutableLiveData<Status<List<Chapter>>>()
    val chapters: LiveData<Status<List<Chapter>>> get() = _chapters

    fun getCourse(courseId: String) {
        launchViewModelScope {
            val result = courseRepository.getCourse(courseId)
            _course.postValue(result)
        }
    }

    fun getChapters(courseId: String) {
        launchViewModelScope {
            val result = courseRepository.getChapters(courseId)
            _chapters.postValue(result)
        }
    }
}