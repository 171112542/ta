package com.mobile.ta.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobile.ta.data.CourseOverviewData
import com.mobile.ta.model.CourseOverview

class HomeViewModel : ViewModel() {
    private var _courseOverviews: MutableLiveData<Array<CourseOverview>> =
        MutableLiveData(CourseOverviewData.data)
    val courseOverviews: LiveData<Array<CourseOverview>>
        get() = _courseOverviews
}