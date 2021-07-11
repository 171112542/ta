package com.mobile.ta.teacher.viewmodel.search

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.information.LevelTag
import com.mobile.ta.model.course.information.TypeTag
import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : BaseViewModel() {
    companion object {
        enum class SortOption {
            A_Z,
            Z_A
        }
    }

    var keyword = ""
    private var _selectedTypeTags = MutableLiveData<ArrayList<TypeTag>>(arrayListOf())
    private var _selectedLevelTags = MutableLiveData<ArrayList<LevelTag>>(arrayListOf())

    val selectedTags = MediatorLiveData<List<String>>().apply {
        val sourceOne = _selectedTypeTags
        val sourceTwo = _selectedLevelTags

        fun getCombinedStrings(
            typeTags: ArrayList<TypeTag>?,
            levelTags: ArrayList<LevelTag>?
        ): ArrayList<String> {
            val result = arrayListOf<String>()
            typeTags?.forEach { result.add(it.toString()) }
            levelTags?.forEach { result.add(it.toString()) }
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

    private var _isSearching = MutableLiveData<Boolean>()
    val isSearching get() = _isSearching
    private var _isFiltered = MutableLiveData(false)
    val isFiltered get() = _isFiltered

    private var _searchResult = MutableLiveData<MutableList<Course>>()
    private var _filteredSearchResult = MutableLiveData<MutableList<Course>>(mutableListOf())
    val filteredSearchResult get() = _filteredSearchResult

    fun performSearch(keyword: String) {
        if (keyword == "") return
        _isSearching.value = true
        this.keyword = keyword
        resetFilter()
        launchViewModelScope {
            val searchResult = courseRepository.searchCourse(keyword.toLowerCase(Locale.ENGLISH))
            _isSearching.postValue(false)
            checkStatus(
                searchResult, {
                    _searchResult.postValue(it)
                    _filteredSearchResult.postValue(
                        it.sortedBy { it.title }.toMutableList()
                    )
                }, {
                    //TODO: Add network failure handler
                }
            )
        }
    }

    fun saveSelectedTags(tags: ArrayList<Any>) {
        _selectedLevelTags.value = arrayListOf()
        _selectedTypeTags.value = arrayListOf()
        tags.map {
            when (it) {
                is TypeTag -> _selectedTypeTags.value?.add(it)
                is LevelTag -> _selectedLevelTags.value?.add(it)
                else -> {
                }
            }
        }
        _selectedTypeTags.value = _selectedTypeTags.value
        _selectedLevelTags.value = _selectedLevelTags.value
    }

    fun saveSelectedSortOption(sortOption: SortOption) {
        _selectedSortOption.value = sortOption
    }

    fun performFilter() {
        _sortFilter.value = _selectedSortOption.value
        _typeTagFilter.value = _selectedTypeTags.value
        _levelTagFilter.value = _selectedLevelTags.value
        var nonfilteredResult = _searchResult.value ?: return
        _isFiltered.value = true

        if (_typeTagFilter.value != null && requireNotNull(_typeTagFilter.value).isNotEmpty()) {
            nonfilteredResult = nonfilteredResult.filter {
                _typeTagFilter.value?.contains(it.type) ?: false
            }.toMutableList()
        }
        if (_levelTagFilter.value != null && requireNotNull(_levelTagFilter.value).isNotEmpty()) {
            nonfilteredResult = nonfilteredResult.filter {
                _levelTagFilter.value?.contains(it.level) ?: false
            }.toMutableList()
        }
        nonfilteredResult =
            if (_selectedSortOption.value == SortOption.A_Z) nonfilteredResult.sortedBy { it.title }
                .toMutableList()
            else nonfilteredResult.sortedByDescending { it.title }.toMutableList()
        _filteredSearchResult.value = nonfilteredResult
    }

    fun resetFilter() {
        _isFiltered.value = false
        _sortFilter.value = SortOption.A_Z
        _typeTagFilter.value = arrayListOf()
        _levelTagFilter.value = arrayListOf()
        _selectedLevelTags.value = arrayListOf()
        _selectedTypeTags.value = arrayListOf()
        _selectedSortOption.value = SortOption.A_Z
    }
}