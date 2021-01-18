package com.mobile.ta.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobile.ta.data.CourseOverviewData
import com.mobile.ta.model.CourseOverview
import java.util.*

class SearchViewModel : ViewModel() {
    private var _hasSearched = MutableLiveData(false)
    val hasSearched get() = _hasSearched

    private var _searchResult = MutableLiveData<List<CourseOverview>>()
    val searchResult get() = _searchResult

    fun performSearch(keyword: String) {
        val searchResult = CourseOverviewData.data.filter {
            it.title.toLowerCase(Locale.ENGLISH).contains(keyword.toLowerCase(Locale.ENGLISH))
        }
        _hasSearched.value = true
        _searchResult.value = searchResult
    }
}