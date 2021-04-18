package com.mobile.ta.viewmodel.courseContent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.ta.model.Status
import com.mobile.ta.model.course.Chapter
import com.mobile.ta.model.course.Course
import com.mobile.ta.repository.CourseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CourseContentViewModel : ViewModel() {
    private var _course = MutableLiveData<Status<Course>>()
    val course: LiveData<Status<Course>> get() = _course
    private var _chapter = MutableLiveData<Status<Chapter>>()
    val chapter: LiveData<Status<Chapter>> get() = _chapter
    private val courseRepository = CourseRepository()

    fun getCourse(courseId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = courseRepository.getCourse(courseId)
            _course.postValue(result)
        }
    }

    fun getChapter(courseId: String, chapterId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = courseRepository.getChapter(courseId, chapterId)
            _chapter.postValue(result)
        }
    }
}