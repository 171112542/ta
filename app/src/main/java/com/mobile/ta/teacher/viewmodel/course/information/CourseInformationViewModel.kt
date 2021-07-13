package com.mobile.ta.teacher.viewmodel.course.information

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.user.User
import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.ui.viewmodel.base.BaseViewModel
import com.mobile.ta.utils.wrapper.status.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourseInformationViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
) : BaseViewModel() {
    private val _course = MutableLiveData<Status<Course>>()
    val course: LiveData<Status<Course>>
        get() = _course
    private val _enrollCourse = MutableLiveData<Status<Boolean>>()
    val enrollCourse: LiveData<Status<Boolean>> get() = _enrollCourse
    private val _relatedCourses = MutableLiveData<MutableList<Course>>()
    val relatedCourses: LiveData<MutableList<Course>> get() = _relatedCourses
    private val _creator = MutableLiveData<User>()
    val creator: LiveData<User> get() = _creator
    private val _prerequisiteCourses = MutableLiveData<MutableList<Course>>()
    val prerequisiteCourses: LiveData<MutableList<Course>> get() = _prerequisiteCourses

    fun getCourse(courseId: String) {
        launchViewModelScope {
            val result = courseRepository.getCourseById(courseId)
            checkStatus(result, {
                getPreRequisiteCourses(it.prerequisiteCourse)
                getRelatedCourses(it.relatedCourse)
                getCreator(it.creatorId)
            })
            _course.postValue(result)
        }
    }

    private fun getCreator(creator: String) {
        launchViewModelScope {
            val user = userRepository.getUserById(creator)
            checkStatus(user, {
                _creator.postValue(it)
            })
        }
    }

    private fun getPreRequisiteCourses(prerequisiteCourses: List<String>) {
        launchViewModelScope {
            val courses = mutableListOf<Course>()
            prerequisiteCourses.forEach {
                val course = courseRepository.getCourseById(it)
                checkStatus(course, { result ->
                    courses.add(result)
                })
            }
            _prerequisiteCourses.postValue(courses)
        }
    }

    private fun getRelatedCourses(relatedCourses: List<String>) {
        launchViewModelScope {
            val courses = mutableListOf<Course>()
            relatedCourses.forEach {
                val course = courseRepository.getCourseById(it)
                checkStatus(course, { result ->
                    courses.add(result)
                })
            }
            _relatedCourses.postValue(courses)
        }
    }
}