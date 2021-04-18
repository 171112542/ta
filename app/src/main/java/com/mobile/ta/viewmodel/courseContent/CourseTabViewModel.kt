package com.mobile.ta.viewmodel.courseContent

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mobile.ta.model.UserCourse

class CourseTabViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _userOngoingCourse = MutableLiveData<List<UserCourse>>()
    val userOngoingCourse: LiveData<List<UserCourse>>
        get() = _userOngoingCourse
    private val _userFinishedCourse = MutableLiveData<List<UserCourse>>()
    val userFinishedCourse: LiveData<List<UserCourse>>
        get() = _userFinishedCourse
}