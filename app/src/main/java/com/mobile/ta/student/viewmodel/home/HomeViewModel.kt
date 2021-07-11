package com.mobile.ta.student.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.course.Course
import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    courseRepository: CourseRepository
) : BaseViewModel() {
    private var _courses: MutableLiveData<MutableList<Course>> = MutableLiveData()
    val courseOverviews: LiveData<MutableList<Course>>
        get() = _courses

    init {
        launchViewModelScope {
            val networkCourses = courseRepository.getAllCourses()
            checkStatus(
                networkCourses, {
                    _courses.postValue(it)
                }, {
                    //TODO: Handle network error
                }
            )
        }
    }
}