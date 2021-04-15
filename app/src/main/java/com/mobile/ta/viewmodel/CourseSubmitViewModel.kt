package com.mobile.ta.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobile.ta.repo.CourseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CourseSubmitViewModel : ViewModel() {
    private val courseRepository = CourseRepository()
    private var _result: MutableLiveData<MutableMap<String, Long>> = MutableLiveData(mutableMapOf())
    val result: LiveData<MutableMap<String, Long>>
        get() = _result

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val submittedChapter =
                courseRepository
                    .getSubmittedChapter("bWAEfZivIK6RyKitZtt4", "jy3EsIxdCVvPn8i0Wl7W")
                    .await()
            _result.postValue(mutableMapOf(
                "correctAnswerCount" to submittedChapter["correctAnswerCount"] as Long,
                "totalAnswerCount" to submittedChapter["totalAnswerCount"] as Long
            ))
        }
    }

    fun retry() {
        courseRepository.resetSubmittedChapter("bWAEfZivIK6RyKitZtt4", "jy3EsIxdCVvPn8i0Wl7W")
    }
}