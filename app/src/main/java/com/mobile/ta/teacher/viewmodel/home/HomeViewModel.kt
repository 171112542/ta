package com.mobile.ta.teacher.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.course.Course
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    courseRepository: CourseRepository,
    authRepository: AuthRepository
) : BaseViewModel() {
    private var _courses: MutableLiveData<MutableList<Course>> = MutableLiveData()
    val courseOverviews: LiveData<MutableList<Course>>
        get() = _courses

    init {
        launchViewModelScope {
            val teacherId = authRepository.getUser()?.uid ?: return@launchViewModelScope
            //TODO: Handle nonauthorized users
            val networkCourses = courseRepository.getAllCoursesByCreatorId(teacherId)
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