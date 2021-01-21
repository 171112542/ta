package com.mobile.ta.viewmodel.courseInfo

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mobile.ta.data.CourseInformationData
import com.mobile.ta.model.courseInfo.CourseInformation
import com.mobile.ta.model.courseInfo.Creator

class CourseInformationViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
): ViewModel() {

    companion object {
        private const val COURSE_INFO = "COURSE_INFO"
        private const val CREATOR_INFO = "CREATOR_INFO"
    }

    private var _courseInfo: MutableLiveData<CourseInformation>
    val courseInfo: LiveData<CourseInformation>
        get() = _courseInfo

    private var _creatorInfo: MutableLiveData<Creator>
    val creatorInfo: LiveData<Creator>
        get() = _creatorInfo

    init {
        _courseInfo = savedStateHandle.getLiveData(COURSE_INFO)
        _creatorInfo = savedStateHandle.getLiveData(CREATOR_INFO)
    }

    fun fetchCourseInfo(id: String) {
        _courseInfo.value = CourseInformationData.data
    }

    fun fetchCreatorInfo(creatorId: String) {
        _creatorInfo.value = CourseInformationData.creator
    }
}