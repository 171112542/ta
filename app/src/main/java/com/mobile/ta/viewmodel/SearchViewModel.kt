package com.mobile.ta.viewmodel

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobile.ta.data.CourseOverviewData
import com.mobile.ta.model.CourseOverview
import com.mobile.ta.model.LevelTag
import com.mobile.ta.model.TypeTag
import java.util.*

class SearchViewModel : ViewModel() {
    companion object {
        enum class SortOption {
            A_Z,
            Z_A,
//            NEWEST,
//            OLDEST
        }
    }

    private var _selectedTypeTags = MutableLiveData<ArrayList<TypeTag>>(arrayListOf())
    private var _selectedLevelTags = MutableLiveData<ArrayList<LevelTag>>(arrayListOf())

    val selectedTags = MediatorLiveData<List<String>>().apply {
        val sourceOne = _selectedTypeTags
        val sourceTwo = _selectedLevelTags

        fun getCombinedStrings(typeTags: ArrayList<TypeTag>?, levelTags: ArrayList<LevelTag>?): ArrayList<String> {
            val result = arrayListOf<String>()
            typeTags?.forEach {
                when (it) {
                    TypeTag.CHEMISTRY -> result.add("Chemistry")
                    TypeTag.BIOLOGY -> result.add("Biology")
                    TypeTag.PHYSICS -> result.add("Physics")
                    TypeTag.MATH -> result.add("Math")
                }
            }
            levelTags?.forEach {
                when (it) {
                    LevelTag.JUNIOR_ONE -> result.add("Junior - 1")
                    LevelTag.JUNIOR_TWO -> result.add("Junior - 2")
                    LevelTag.JUNIOR_THREE -> result.add("Junior - 3")
                    LevelTag.SENIOR_ONE -> result.add("Senior - 1")
                    LevelTag.SENIOR_TWO -> result.add("Senior - 2")
                    LevelTag.SENIOR_THREE -> result.add("Senior - 3")
                }
            }
            return result
        }

        addSource(sourceOne) {
            this.value = getCombinedStrings(sourceOne.value, sourceTwo.value)
        }
        addSource(sourceTwo) {
            this.value = getCombinedStrings(sourceOne.value, sourceTwo.value)
        }
    }

    private var _selectedSortOption = MutableLiveData(SortOption.A_Z)
    val selectedSortOption get() = _selectedSortOption

    private var _sortFilter = MutableLiveData(SortOption.A_Z)
    private var _typeTagFilter = MutableLiveData<ArrayList<TypeTag>>(arrayListOf())
    private var _levelTagFilter = MutableLiveData<ArrayList<LevelTag>>(arrayListOf())

    private var _hasSearched = MutableLiveData(false)
    val hasSearched get() = _hasSearched

    private var _searchResult = MutableLiveData<List<CourseOverview>>(listOf())
    private var _filteredSearchResult = MutableLiveData<List<CourseOverview>>(listOf())
    val filteredSearchResult get() = _filteredSearchResult

    fun performSearch(keyword: String) {
        resetFilter()
        val searchResult = CourseOverviewData.data.filter {
            it.title.toLowerCase(Locale.ENGLISH).contains(keyword.toLowerCase(Locale.ENGLISH))
        }
        _hasSearched.value = true
        _searchResult.value = searchResult
        _filteredSearchResult.value = searchResult.sortedBy { it.title }
    }

    fun saveSelectedTags(tags: ArrayList<Any>) {
        _selectedLevelTags.value = arrayListOf()
        _selectedTypeTags.value = arrayListOf()
        tags.map {
            when (it) {
                is TypeTag -> _selectedTypeTags.value?.add(it)
                is LevelTag -> _selectedLevelTags.value?.add(it)
                else ->  {}
            }
        }
        _selectedTypeTags.value = _selectedTypeTags.value
        _selectedLevelTags.value = _selectedLevelTags.value
    }

    fun saveSelectedSortOption(sortOption: SortOption) {
        _selectedSortOption.value = sortOption
    }

    fun resetSelectedToFault() {
        _selectedTypeTags.value = arrayListOf()
        _selectedLevelTags.value = arrayListOf()
        _selectedSortOption.value = SortOption.A_Z
    }

    fun performFilter() {
        _sortFilter.value = _selectedSortOption.value
        _typeTagFilter.value = _selectedTypeTags.value
        _levelTagFilter.value = _selectedLevelTags.value
        var nonfilteredResult = _searchResult.value

        if (_typeTagFilter.value != null && _typeTagFilter.value!!.isNotEmpty()) {
            nonfilteredResult = nonfilteredResult?.filter {
                _typeTagFilter.value?.contains(it.type) ?: false
            }
        }
        if (_levelTagFilter.value != null && _levelTagFilter.value!!.isNotEmpty()) {
            nonfilteredResult = nonfilteredResult?.filter {
                _levelTagFilter.value?.contains(it.level) ?: false
            }
        }
        nonfilteredResult =
            if (_selectedSortOption.value == SortOption.A_Z) nonfilteredResult?.sortedBy { it.title }
            else nonfilteredResult?.sortedByDescending { it.title }
        _filteredSearchResult.value = nonfilteredResult
    }

    private fun resetFilter() {
        _sortFilter.value = SortOption.A_Z
        _typeTagFilter.value = arrayListOf()
        _levelTagFilter.value = arrayListOf()
        _selectedLevelTags.value = arrayListOf()
        _selectedTypeTags.value = arrayListOf()
        _selectedSortOption.value = SortOption.A_Z
    }
}