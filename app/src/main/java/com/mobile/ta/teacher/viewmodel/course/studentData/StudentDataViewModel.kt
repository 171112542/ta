package com.mobile.ta.teacher.viewmodel.course.studentData

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.mobile.ta.model.studentProgress.StudentProgress
import com.mobile.ta.repository.StudentProgressRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StudentDataViewModel @Inject constructor(
    studentProgressRepository: StudentProgressRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val courseId = savedStateHandle.get<String>("courseId") ?: ""
    private val _studentProgressData = MutableLiveData<List<StudentProgress>>()
    val studentProgressData: LiveData<List<StudentProgress>>
        get() = _studentProgressData

    private val _searchedStudentProgressData = MutableLiveData<List<StudentProgress>>()
    val searchedStudentProgressData: LiveData<List<StudentProgress>>
        get() = _searchedStudentProgressData

    init {
        launchViewModelScope {
            val networkStudentProgressData =
                studentProgressRepository.getStudentProgressByCourseId(courseId)
            checkStatus(
                networkStudentProgressData, {
                    _studentProgressData.postValue(it.toList())
                    _searchedStudentProgressData.postValue(it.toList())
                }, {
                    //TODO: Handle network error
                }
            )
        }
    }

    fun performSearch(email: String) {
        if (email == "") {
            _searchedStudentProgressData.value = _studentProgressData.value ?: listOf()
        }
        else {
            val emailSearched = email.toLowerCase(Locale.ENGLISH)
            _searchedStudentProgressData.value = _studentProgressData.value?.filter {
                it.student?.email?.toLowerCase(Locale.ENGLISH)?.indexOf(emailSearched) != -1
            }
        }
    }
}