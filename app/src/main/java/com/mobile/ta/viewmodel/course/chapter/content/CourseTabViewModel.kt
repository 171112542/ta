package com.mobile.ta.viewmodel.course.chapter.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mobile.ta.model.user.course.UserCourse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourseTabViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _userOngoingCourse = MutableLiveData<List<UserCourse>>()
    val userOngoingCourse: LiveData<List<UserCourse>>
        get() = _userOngoingCourse
    private val _userFinishedCourse = MutableLiveData<List<UserCourse>>()
    val userFinishedCourse: LiveData<List<UserCourse>>
        get() = _userFinishedCourse
}